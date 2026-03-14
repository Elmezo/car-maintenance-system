// Car Types
export interface Car {
  id: number;
  plateNumber: string;
  brand: string;
  model: string;
  year: number;
  color?: string;
  vin?: string;
  currentMileage: number;
  engineType: 'petrol' | 'diesel' | 'electric' | 'hybrid';
  transmission: 'manual' | 'automatic' | 'cvt';
  fuelCapacity?: number;
  ownerName?: string;
  ownerPhone?: string;
  ownerEmail?: string;
  purchaseDate?: string;
  warrantyExpiry?: string;
  imageUrl?: string;
  status: 'active' | 'inactive' | 'sold';
  notes?: string;
  createdAt: string;
  updatedAt: string;
  carAge?: number;
  warrantyActive?: boolean;
  maintenanceCount?: number;
  failureCount?: number;
  totalMaintenanceCost?: number;
  totalRepairCost?: number;
  healthScore?: number;
}

// Maintenance Types
export interface MaintenanceRecord {
  id: number;
  carId: number;
  maintenanceTypeId: number;
  maintenanceTypeName?: string;
  maintenanceTypeNameAr?: string;
  serviceDate: string;
  mileageAtService: number;
  cost: number;
  laborCost?: number;
  partsCost?: number;
  workshopName?: string;
  workshopLocation?: string;
  technicianName?: string;
  description?: string;
  partsUsed?: string;
  nextServiceMileage?: number;
  nextServiceDate?: string;
  warrantyMonths?: number;
  invoiceNumber?: string;
  receiptImageUrl?: string;
  status: 'scheduled' | 'in_progress' | 'completed' | 'cancelled';
  rating?: number;
  notes?: string;
  createdAt: string;
  updatedAt: string;
  carPlateNumber?: string;
  carBrand?: string;
  carModel?: string;
}

export interface MaintenanceType {
  id: number;
  name: string;
  nameAr?: string;
  category: 'routine' | 'preventive' | 'corrective' | 'emergency';
  description?: string;
  recommendedIntervalKm?: number;
  recommendedIntervalMonths?: number;
  estimatedDurationHours?: number;
  priority: 'low' | 'medium' | 'high' | 'critical';
  isActive: boolean;
}

// Failure Types
export interface Failure {
  id: number;
  carId: number;
  failureTypeId: number;
  failureTypeName?: string;
  failureTypeNameAr?: string;
  failureCategory?: string;
  failureDate: string;
  mileageAtFailure: number;
  severity: 'minor' | 'moderate' | 'major' | 'critical';
  description?: string;
  symptoms?: string;
  rootCause?: string;
  weatherConditions?: string;
  drivingConditions?: string;
  isRecurring?: boolean;
  parentFailureId?: number;
  createdAt: string;
  updatedAt: string;
  carPlateNumber?: string;
  carBrand?: string;
  carModel?: string;
  isRepaired?: boolean;
  repairCount?: number;
}

export interface FailureType {
  id: number;
  name: string;
  nameAr?: string;
  category: string;
  description?: string;
  commonCauses?: string;
  preventionTips?: string;
}

// Repair Types
export interface Repair {
  id: number;
  failureId: number;
  carId: number;
  repairDate: string;
  mileageAtRepair: number;
  cost: number;
  laborCost?: number;
  partsCost?: number;
  workshopName?: string;
  technicianName?: string;
  description?: string;
  partsReplaced?: string;
  repairMethod?: string;
  warrantyMonths?: number;
  invoiceNumber?: string;
  status: 'pending' | 'in_progress' | 'completed' | 'partially_completed';
  isSuccessful?: boolean;
  followUpRequired?: boolean;
  followUpDate?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
  failureTypeName?: string;
  failureSeverity?: string;
}

// Prediction Types
export interface Prediction {
  id: number;
  carId: number;
  predictionType: 'maintenance' | 'failure' | 'cost';
  predictionDate: string;
  predictedEvent: string;
  predictedDate?: string;
  predictedMileage?: number;
  probability?: number;
  confidenceLevel: 'low' | 'medium' | 'high';
  contributingFactors?: string;
  recommendations?: string;
  modelVersion?: string;
  isAccurate?: boolean;
  actualOutcome?: string;
  feedbackDate?: string;
  createdAt: string;
  carPlateNumber?: string;
  carBrand?: string;
  carModel?: string;
  estimatedCost?: number;
}

// Dashboard Statistics Types
export interface DashboardStats {
  totalCars: number;
  activeCars: number;
  totalMaintenanceRecords: number;
  totalFailures: number;
  totalMaintenanceCost: number;
  totalRepairCost: number;
  grandTotalCost: number;
  averageMileage: number;
  averageMaintenanceCostPerCar: number;
  averageRepairCostPerCar: number;
  monthlyCosts: MonthlyCost[];
  topFailures: FailureStats[];
  maintenanceStats: MaintenanceStats[];
  healthDistribution: Record<string, number>;
  upcomingMaintenance: UpcomingMaintenance[];
}

export interface MonthlyCost {
  year: number;
  month: number;
  maintenanceCost: number;
  repairCost: number;
  maintenanceCount: number;
  failureCount: number;
}

export interface FailureStats {
  failureTypeName: string;
  failureTypeNameAr?: string;
  count: number;
  totalCost: number;
  averageCost: number;
}

export interface MaintenanceStats {
  maintenanceTypeName: string;
  maintenanceTypeNameAr?: string;
  count: number;
  totalCost: number;
  averageCost: number;
}

export interface UpcomingMaintenance {
  carId: number;
  plateNumber: string;
  brand: string;
  model: string;
  maintenanceType: string;
  dueDate?: string;
  dueMileage?: number;
  daysUntilDue?: number;
  kmUntilDue?: number;
  priority: string;
}

// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
  error?: string;
}
