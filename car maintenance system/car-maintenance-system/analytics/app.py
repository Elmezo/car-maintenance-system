"""
Smart Car Maintenance Analytics Service
Machine Learning based predictions for car maintenance and failures
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
from flask_restful import Api, Resource
import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier, GradientBoostingRegressor
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.model_selection import train_test_split
import joblib
import os
from datetime import datetime, timedelta
from typing import Dict, List, Any
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)
api = Api(app)

# Model storage
MODEL_DIR = 'models'
os.makedirs(MODEL_DIR, exist_ok=True)

class MaintenancePredictorModel:
    """
    Machine Learning model for predicting maintenance needs
    """
    
    def __init__(self):
        self.maintenance_model = None
        self.failure_model = None
        self.cost_model = None
        self.scaler = StandardScaler()
        self.label_encoders = {}
        self.is_trained = False
        
    def prepare_features(self, data: Dict) -> np.ndarray:
        """
        Prepare features from input data
        """
        features = []
        
        # Basic car features
        features.append(data.get('current_mileage', 0))
        features.append(data.get('car_age', 0))
        
        # Encode categorical features
        engine_type = data.get('engine_type', 'petrol')
        if 'engine_type' not in self.label_encoders:
            self.label_encoders['engine_type'] = LabelEncoder()
            self.label_encoders['engine_type'].fit(['petrol', 'diesel', 'electric', 'hybrid'])
        features.append(self.label_encoders['engine_type'].transform([engine_type])[0])
        
        transmission = data.get('transmission', 'automatic')
        if 'transmission' not in self.label_encoders:
            self.label_encoders['transmission'] = LabelEncoder()
            self.label_encoders['transmission'].fit(['manual', 'automatic', 'cvt'])
        features.append(self.label_encoders['transmission'].transform([transmission])[0])
        
        # Maintenance history features
        maintenance_history = data.get('maintenance_history', [])
        if maintenance_history:
            avg_days_since = np.mean([m.get('days_since_last_service', 0) for m in maintenance_history])
            avg_km_since = np.mean([m.get('km_since_last_service', 0) for m in maintenance_history])
        else:
            avg_days_since = 180  # Default
            avg_km_since = 5000   # Default
        
        features.append(avg_days_since)
        features.append(avg_km_since)
        
        # Failure history features
        failure_history = data.get('failure_history', [])
        if failure_history:
            failure_count = len(failure_history)
            recurring_count = sum(1 for f in failure_history if f.get('is_recurring', False))
            recent_failures = sum(1 for f in failure_history if f.get('days_since_failure', 365) < 90)
        else:
            failure_count = 0
            recurring_count = 0
            recent_failures = 0
        
        features.extend([failure_count, recurring_count, recent_failures])
        
        return np.array(features).reshape(1, -1)
    
    def train(self, training_data: List[Dict]):
        """
        Train the prediction models
        """
        if not training_data:
            logger.warning("No training data provided, using synthetic data")
            training_data = self._generate_synthetic_data()
        
        df = pd.DataFrame(training_data)
        
        # Prepare features
        X = []
        y_maintenance = []
        y_failure = []
        y_cost = []
        
        for record in training_data:
            features = self.prepare_features(record).flatten()
            X.append(features)
            y_maintenance.append(record.get('needs_maintenance', 0))
            y_failure.append(record.get('failure_probability', 0))
            y_cost.append(record.get('estimated_cost', 0))
        
        X = np.array(X)
        
        # Scale features
        X = self.scaler.fit_transform(X)
        
        # Train maintenance prediction model
        self.maintenance_model = RandomForestClassifier(
            n_estimators=100,
            max_depth=10,
            random_state=42
        )
        self.maintenance_model.fit(X, y_maintenance)
        
        # Train failure prediction model
        self.failure_model = GradientBoostingRegressor(
            n_estimators=100,
            max_depth=5,
            random_state=42
        )
        self.failure_model.fit(X, y_failure)
        
        # Train cost prediction model
        self.cost_model = GradientBoostingRegressor(
            n_estimators=100,
            max_depth=5,
            random_state=42
        )
        self.cost_model.fit(X, y_cost)
        
        self.is_trained = True
        logger.info("Models trained successfully")
        
    def _generate_synthetic_data(self) -> List[Dict]:
        """
        Generate synthetic training data
        """
        np.random.seed(42)
        data = []
        
        for _ in range(1000):
            mileage = np.random.randint(10000, 200000)
            age = np.random.randint(1, 15)
            engine_type = np.random.choice(['petrol', 'diesel', 'electric', 'hybrid'])
            transmission = np.random.choice(['manual', 'automatic', 'cvt'])
            
            # Maintenance probability increases with mileage and age
            needs_maintenance = 1 if (mileage % 5000 < 500 or age > 10) else 0
            
            # Failure probability
            failure_prob = min(0.9, (mileage / 200000) * 0.5 + (age / 15) * 0.3)
            failure_prob += 0.1 if engine_type == 'diesel' else 0
            failure_prob = min(1.0, failure_prob)
            
            # Estimated cost
            base_cost = 200
            if mileage > 100000:
                base_cost += 300
            if age > 5:
                base_cost += age * 20
            
            data.append({
                'current_mileage': mileage,
                'car_age': age,
                'engine_type': engine_type,
                'transmission': transmission,
                'maintenance_history': [],
                'failure_history': [],
                'needs_maintenance': needs_maintenance,
                'failure_probability': failure_prob,
                'estimated_cost': base_cost + np.random.randint(-50, 150)
            })
        
        return data
    
    def predict(self, data: Dict) -> List[Dict]:
        """
        Generate predictions for a car
        """
        if not self.is_trained:
            self.train([])
        
        features = self.prepare_features(data)
        features_scaled = self.scaler.transform(features)
        
        predictions = []
        
        # Maintenance predictions
        maintenance_proba = self.maintenance_model.predict_proba(features_scaled)[0]
        needs_maintenance = maintenance_proba[1] if len(maintenance_proba) > 1 else maintenance_proba[0]
        
        if needs_maintenance > 0.5:
            predictions.append({
                'prediction_type': 'maintenance',
                'predicted_event': 'General Maintenance Due',
                'probability': round(float(needs_maintenance), 3),
                'confidence_level': 'high' if needs_maintenance > 0.8 else 'medium',
                'recommendations': 'Schedule general maintenance service'
            })
        
        # Oil change prediction
        oil_change_prob = self._calculate_oil_change_probability(data)
        if oil_change_prob > 0.3:
            predictions.append({
                'prediction_type': 'maintenance',
                'predicted_event': 'Oil Change',
                'predicted_mileage': data.get('current_mileage', 0) + int(5000 * (1 - oil_change_prob)),
                'predicted_date': (datetime.now() + timedelta(days=int(30 * (1 - oil_change_prob) * 2))).strftime('%Y-%m-%d'),
                'probability': round(oil_change_prob, 3),
                'confidence_level': 'high' if oil_change_prob > 0.7 else 'medium',
                'recommendations': f'Schedule oil change within the next {int(5000 * (1 - oil_change_prob))} km'
            })
        
        # Brake prediction
        brake_prob = self._calculate_brake_probability(data)
        if brake_prob > 0.4:
            predictions.append({
                'prediction_type': 'maintenance',
                'predicted_event': 'Brake Pads Replacement',
                'predicted_mileage': data.get('current_mileage', 0) + int(10000 * (1 - brake_prob)),
                'probability': round(brake_prob, 3),
                'confidence_level': 'medium',
                'recommendations': 'Inspect brake pads during next service'
            })
        
        # Failure predictions
        failure_prob = self.failure_model.predict(features_scaled)[0]
        failure_prob = min(1.0, max(0.0, failure_prob))
        
        if failure_prob > 0.3:
            potential_failures = self._identify_potential_failures(data, failure_prob)
            for failure in potential_failures:
                predictions.append({
                    'prediction_type': 'failure',
                    'predicted_event': failure['event'],
                    'predicted_date': failure['date'],
                    'probability': round(failure['probability'], 3),
                    'confidence_level': 'medium' if failure['probability'] > 0.5 else 'low',
                    'contributing_factors': failure['factors'],
                    'recommendations': failure['recommendations']
                })
        
        # Cost prediction
        estimated_cost = self.cost_model.predict(features_scaled)[0]
        predictions.append({
            'prediction_type': 'cost',
            'predicted_event': 'Annual Maintenance Cost',
            'probability': 1.0,
            'confidence_level': 'medium',
            'estimated_cost': round(float(estimated_cost), 2),
            'recommendations': 'Budget for upcoming maintenance expenses'
        })
        
        return predictions
    
    def _calculate_oil_change_probability(self, data: Dict) -> float:
        """Calculate probability of needing oil change"""
        maintenance_history = data.get('maintenance_history', [])
        current_mileage = data.get('current_mileage', 0)
        
        # Find last oil change
        last_oil_change_km = None
        for m in maintenance_history:
            if 'oil' in m.get('maintenance_type', '').lower():
                last_oil_change_km = m.get('km_since_last_service', 0)
                break
        
        if last_oil_change_km is None:
            # No record, assume it's due
            return 0.9
        
        # Calculate probability based on km since last change
        km_ratio = last_oil_change_km / 5000
        return min(1.0, km_ratio)
    
    def _calculate_brake_probability(self, data: Dict) -> float:
        """Calculate probability of needing brake service"""
        current_mileage = data.get('current_mileage', 0)
        car_age = data.get('car_age', 0)
        
        # Base probability on mileage
        prob = (current_mileage / 100000) * 0.5
        
        # Add age factor
        prob += (car_age / 15) * 0.3
        
        return min(1.0, prob)
    
    def _identify_potential_failures(self, data: Dict, base_prob: float) -> List[Dict]:
        """Identify potential failures based on car condition"""
        failures = []
        current_mileage = data.get('current_mileage', 0)
        car_age = data.get('car_age', 0)
        failure_history = data.get('failure_history', [])
        
        # Check for recurring failures
        for f in failure_history:
            if f.get('is_recurring', False):
                failures.append({
                    'event': f"{f.get('failure_type', 'Unknown')} - Recurring",
                    'date': (datetime.now() + timedelta(days=60)).strftime('%Y-%m-%d'),
                    'probability': min(0.8, base_prob + 0.3),
                    'factors': f"Previous occurrence at {f.get('days_since_failure', 0)} days ago",
                    'recommendations': 'Monitor closely and consider preventive measures'
                })
        
        # Battery failure (age related)
        if car_age > 3:
            prob = (car_age - 3) * 0.15
            if prob > 0.2:
                failures.append({
                    'event': 'Battery Failure',
                    'date': (datetime.now() + timedelta(days=90)).strftime('%Y-%m-%d'),
                    'probability': min(0.7, prob),
                    'factors': f'Car age: {car_age} years',
                    'recommendations': 'Consider battery replacement in next service'
                })
        
        # Timing belt (high mileage)
        if current_mileage > 80000 and current_mileage < 120000:
            failures.append({
                'event': 'Timing Belt Wear',
                'date': (datetime.now() + timedelta(days=180)).strftime('%Y-%m-%d'),
                'probability': 0.6,
                'factors': f'Mileage: {current_mileage} km (recommended change at 100,000 km)',
                'recommendations': 'Schedule timing belt inspection'
            })
        
        return failures

    def save_models(self):
        """Save trained models to disk"""
        if self.is_trained:
            joblib.dump({
                'maintenance_model': self.maintenance_model,
                'failure_model': self.failure_model,
                'cost_model': self.cost_model,
                'scaler': self.scaler,
                'label_encoders': self.label_encoders,
                'is_trained': self.is_trained
            }, os.path.join(MODEL_DIR, 'predictor_models.joblib'))
            logger.info("Models saved successfully")
    
    def load_models(self):
        """Load trained models from disk"""
        model_path = os.path.join(MODEL_DIR, 'predictor_models.joblib')
        if os.path.exists(model_path):
            data = joblib.load(model_path)
            self.maintenance_model = data['maintenance_model']
            self.failure_model = data['failure_model']
            self.cost_model = data['cost_model']
            self.scaler = data['scaler']
            self.label_encoders = data['label_encoders']
            self.is_trained = data['is_trained']
            logger.info("Models loaded successfully")
            return True
        return False


# Initialize predictor
predictor = MaintenancePredictorModel()

# Try to load existing models
if not predictor.load_models():
    # Train with synthetic data if no models exist
    predictor.train([])
    predictor.save_models()


class PredictResource(Resource):
    """
    Resource for generating predictions
    """
    
    def post(self):
        """
        Generate predictions for a car
        """
        try:
            data = request.get_json()
            logger.info(f"Received prediction request for car ID: {data.get('car_id')}")
            
            predictions = predictor.predict(data)
            
            return jsonify(predictions)
        
        except Exception as e:
            logger.error(f"Prediction error: {str(e)}")
            return {'error': str(e)}, 500


class TrainResource(Resource):
    """
    Resource for training models
    """
    
    def post(self):
        """
        Train models with provided data
        """
        try:
            data = request.get_json()
            logger.info(f"Received training request with {len(data)} records")
            
            predictor.train(data)
            predictor.save_models()
            
            return {'message': 'Models trained successfully', 'status': 'success'}
        
        except Exception as e:
            logger.error(f"Training error: {str(e)}")
            return {'error': str(e)}, 500


class HealthResource(Resource):
    """
    Health check endpoint
    """
    
    def get(self):
        return {
            'status': 'healthy',
            'service': 'Car Maintenance Analytics',
            'model_trained': predictor.is_trained,
            'timestamp': datetime.now().isoformat()
        }


class AnalysisResource(Resource):
    """
    Resource for detailed analysis
    """
    
    def post(self):
        """
        Perform detailed analysis on car data
        """
        try:
            data = request.get_json()
            
            analysis = {
                'car_health_score': self._calculate_health_score(data),
                'maintenance_score': self._calculate_maintenance_score(data),
                'risk_assessment': self._assess_risk(data),
                'recommendations': self._generate_recommendations(data)
            }
            
            return jsonify(analysis)
        
        except Exception as e:
            logger.error(f"Analysis error: {str(e)}")
            return {'error': str(e)}, 500
    
    def _calculate_health_score(self, data: Dict) -> int:
        """Calculate overall car health score"""
        score = 100
        
        # Deduct for mileage
        mileage = data.get('current_mileage', 0)
        if mileage > 100000:
            score -= min(25, (mileage - 100000) // 10000)
        
        # Deduct for age
        age = data.get('car_age', 0)
        score -= min(25, age * 3)
        
        # Deduct for failures
        failures = data.get('failure_history', [])
        score -= min(25, len(failures) * 5)
        
        # Deduct for overdue maintenance
        maintenance = data.get('maintenance_history', [])
        overdue = sum(1 for m in maintenance if m.get('days_since_last_service', 0) > 
                     (m.get('recommended_interval_months', 6) * 30))
        score -= min(25, overdue * 10)
        
        return max(0, score)
    
    def _calculate_maintenance_score(self, data: Dict) -> int:
        """Calculate maintenance compliance score"""
        score = 100
        maintenance = data.get('maintenance_history', [])
        
        for m in maintenance:
            recommended_days = m.get('recommended_interval_months', 6) * 30
            actual_days = m.get('days_since_last_service', 0)
            
            if actual_days > recommended_days * 1.5:
                score -= 20
            elif actual_days > recommended_days:
                score -= 10
        
        return max(0, score)
    
    def _assess_risk(self, data: Dict) -> Dict:
        """Assess risk level for various components"""
        risks = {}
        
        mileage = data.get('current_mileage', 0)
        age = data.get('car_age', 0)
        failures = data.get('failure_history', [])
        
        # Engine risk
        risks['engine'] = 'high' if mileage > 150000 else 'medium' if mileage > 100000 else 'low'
        
        # Transmission risk
        risks['transmission'] = 'high' if mileage > 180000 else 'medium' if mileage > 120000 else 'low'
        
        # Electrical risk
        risks['electrical'] = 'high' if age > 10 else 'medium' if age > 5 else 'low'
        
        # Brake risk
        brake_failures = [f for f in failures if 'brake' in f.get('failure_type', '').lower()]
        risks['brakes'] = 'high' if brake_failures else 'medium' if mileage > 60000 else 'low'
        
        # Overall risk
        high_risks = sum(1 for v in risks.values() if v == 'high')
        risks['overall'] = 'high' if high_risks >= 2 else 'medium' if high_risks == 1 else 'low'
        
        return risks
    
    def _generate_recommendations(self, data: Dict) -> List[str]:
        """Generate actionable recommendations"""
        recommendations = []
        
        mileage = data.get('current_mileage', 0)
        age = data.get('car_age', 0)
        maintenance = data.get('maintenance_history', [])
        failures = data.get('failure_history', [])
        
        # Check oil change
        oil_maintenance = [m for m in maintenance if 'oil' in m.get('maintenance_type', '').lower()]
        if not oil_maintenance or oil_maintenance[0].get('km_since_last_service', 5000) > 4000:
            recommendations.append("Schedule oil change within the next 1,000 km")
        
        # Check brakes
        if mileage > 50000:
            recommendations.append("Inspect brake pads and discs during next service")
        
        # Check timing belt
        if 80000 < mileage < 120000:
            recommendations.append("Consider timing belt replacement before reaching 100,000 km")
        
        # Check battery
        if age > 3:
            recommendations.append("Test battery health during next service")
        
        # Check for recurring failures
        recurring = [f for f in failures if f.get('is_recurring', False)]
        if recurring:
            recommendations.append(f"Address {len(recurring)} recurring failure(s) with permanent solutions")
        
        # General recommendations
        if age > 5:
            recommendations.append("Consider comprehensive vehicle inspection")
        
        return recommendations


# Register resources
api.add_resource(PredictResource, '/predict')
api.add_resource(TrainResource, '/train')
api.add_resource(HealthResource, '/health')
api.add_resource(AnalysisResource, '/analyze')


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
