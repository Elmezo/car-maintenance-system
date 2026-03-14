const API_BASE_URL = '/api';

// Backend port for the Spring Boot API
const BACKEND_PORT = 8080;

class ApiClient {
  private baseUrl: string;

  constructor() {
    this.baseUrl = API_BASE_URL;
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${this.baseUrl}${endpoint}?XTransformPort=${BACKEND_PORT}`;
    
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    const response = await fetch(url, {
      ...options,
      headers,
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: 'An error occurred' }));
      throw new Error(error.message || `HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    return data.data || data;
  }

  // Cars API
  async getCars() {
    return this.request<any[]>('/cars');
  }

  async getCar(id: number) {
    return this.request<any>(`/cars/${id}`);
  }

  async createCar(car: Partial<any>) {
    return this.request<any>('/cars', {
      method: 'POST',
      body: JSON.stringify(car),
    });
  }

  async updateCar(id: number, car: Partial<any>) {
    return this.request<any>(`/cars/${id}`, {
      method: 'PUT',
      body: JSON.stringify(car),
    });
  }

  async deleteCar(id: number) {
    return this.request<void>(`/cars/${id}`, {
      method: 'DELETE',
    });
  }

  async updateMileage(id: number, mileage: number) {
    return this.request<any>(`/cars/${id}/mileage?mileage=${mileage}`, {
      method: 'PATCH',
    });
  }

  // Maintenance API
  async getMaintenanceRecords(carId: number) {
    return this.request<any[]>(`/maintenance/car/${carId}`);
  }

  async getMaintenanceRecord(id: number) {
    return this.request<any>(`/maintenance/${id}`);
  }

  async createMaintenanceRecord(record: Partial<any>) {
    return this.request<any>('/maintenance', {
      method: 'POST',
      body: JSON.stringify(record),
    });
  }

  async updateMaintenanceRecord(id: number, record: Partial<any>) {
    return this.request<any>(`/maintenance/${id}`, {
      method: 'PUT',
      body: JSON.stringify(record),
    });
  }

  async deleteMaintenanceRecord(id: number) {
    return this.request<void>(`/maintenance/${id}`, {
      method: 'DELETE',
    });
  }

  async getUpcomingMaintenance() {
    return this.request<any[]>('/maintenance/upcoming');
  }

  // Failures API
  async getFailures(carId: number) {
    return this.request<any[]>(`/failures/car/${carId}`);
  }

  async getFailure(id: number) {
    return this.request<any>(`/failures/${id}`);
  }

  async createFailure(failure: Partial<any>) {
    return this.request<any>('/failures', {
      method: 'POST',
      body: JSON.stringify(failure),
    });
  }

  async updateFailure(id: number, failure: Partial<any>) {
    return this.request<any>(`/failures/${id}`, {
      method: 'PUT',
      body: JSON.stringify(failure),
    });
  }

  async deleteFailure(id: number) {
    return this.request<void>(`/failures/${id}`, {
      method: 'DELETE',
    });
  }

  async getMajorFailures(carId: number) {
    return this.request<any[]>(`/failures/car/${carId}/major`);
  }

  async getMostCommonFailures(months: number = 12) {
    return this.request<any[]>(`/failures/most-common?months=${months}`);
  }

  // Repairs API
  async addRepair(failureId: number, repair: Partial<any>) {
    return this.request<any>(`/failures/${failureId}/repairs`, {
      method: 'POST',
      body: JSON.stringify(repair),
    });
  }

  async getRepairs(failureId: number) {
    return this.request<any[]>(`/failures/${failureId}/repairs`);
  }

  // Analytics API
  async getDashboardStats() {
    return this.request<any>('/analytics/dashboard');
  }

  async getPredictions(carId: number) {
    return this.request<any[]>(`/analytics/predictions/${carId}`);
  }

  async generatePredictions(carId: number) {
    return this.request<any[]>(`/analytics/predict/${carId}`, {
      method: 'POST',
    });
  }
}

export const apiClient = new ApiClient();
