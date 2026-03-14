'use client';

import { useState, useEffect } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Toaster } from '@/components/ui/toaster';
import { useToast } from '@/hooks/use-toast';
import { 
  LayoutDashboard, 
  Car as CarIcon, 
  Wrench, 
  AlertTriangle,
  RefreshCw
} from 'lucide-react';

import { StatsCards, HealthScoreCard } from '@/components/dashboard/stats-cards';
import { UpcomingMaintenanceCard } from '@/components/dashboard/upcoming-maintenance';
import { MonthlyCostChart, FailureStatsChart, MaintenanceStatsChart } from '@/components/dashboard/charts';
import { CarsList } from '@/components/cars/cars-list';
import { CarDetails } from '@/components/cars/car-details';
import { CarDialog, MaintenanceDialog, FailureDialog } from '@/components/cars/car-dialogs';

import type { 
  Car, 
  MaintenanceRecord, 
  Failure, 
  Prediction, 
  DashboardStats 
} from '@/types';

// Mock data for demo
const mockCars: Car[] = [
  {
    id: 1,
    plateNumber: 'ABC-1234',
    brand: 'Toyota',
    model: 'Camry',
    year: 2020,
    color: 'White',
    currentMileage: 45000,
    engineType: 'petrol',
    transmission: 'automatic',
    ownerName: 'أحمد محمد',
    ownerPhone: '+201234567890',
    status: 'active',
    createdAt: '2023-01-15',
    updatedAt: '2024-01-15',
    carAge: 4,
    healthScore: 85,
    maintenanceCount: 5,
    failureCount: 2,
    totalMaintenanceCost: 3500,
    totalRepairCost: 500,
  },
  {
    id: 2,
    plateNumber: 'XYZ-5678',
    brand: 'Honda',
    model: 'Civic',
    year: 2019,
    color: 'Black',
    currentMileage: 62000,
    engineType: 'petrol',
    transmission: 'automatic',
    ownerName: 'سارة علي',
    ownerPhone: '+201234567891',
    status: 'active',
    createdAt: '2023-02-20',
    updatedAt: '2024-02-20',
    carAge: 5,
    healthScore: 72,
    maintenanceCount: 8,
    failureCount: 3,
    totalMaintenanceCost: 5200,
    totalRepairCost: 1200,
  },
  {
    id: 3,
    plateNumber: 'DEF-9012',
    brand: 'BMW',
    model: '320i',
    year: 2021,
    color: 'Blue',
    currentMileage: 28000,
    engineType: 'petrol',
    transmission: 'automatic',
    ownerName: 'محمد حسن',
    ownerPhone: '+201234567892',
    status: 'active',
    createdAt: '2023-03-10',
    updatedAt: '2024-03-10',
    carAge: 3,
    healthScore: 92,
    maintenanceCount: 3,
    failureCount: 0,
    totalMaintenanceCost: 2800,
    totalRepairCost: 0,
  },
];

const mockMaintenanceRecords: MaintenanceRecord[] = [
  {
    id: 1,
    carId: 1,
    maintenanceTypeId: 1,
    maintenanceTypeName: 'تغيير الزيت',
    serviceDate: '2024-01-15',
    mileageAtService: 40000,
    cost: 350,
    workshopName: 'Auto Service Center',
    status: 'completed',
    createdAt: '2024-01-15',
    updatedAt: '2024-01-15',
  },
  {
    id: 2,
    carId: 1,
    maintenanceTypeId: 6,
    maintenanceTypeName: 'تغيير تيل الفرامل',
    serviceDate: '2023-08-20',
    mileageAtService: 35000,
    cost: 800,
    workshopName: 'Brake Masters',
    status: 'completed',
    createdAt: '2023-08-20',
    updatedAt: '2023-08-20',
  },
];

const mockFailures: Failure[] = [
  {
    id: 1,
    carId: 1,
    failureTypeId: 3,
    failureTypeName: 'تسريب زيت',
    failureDate: '2023-12-10',
    mileageAtFailure: 38000,
    severity: 'minor',
    description: 'تسريب صغير بالقرب من فلتر الزيت',
    isRepaired: true,
    createdAt: '2023-12-10',
    updatedAt: '2023-12-15',
  },
];

const mockPredictions: Prediction[] = [
  {
    id: 1,
    carId: 1,
    predictionType: 'maintenance',
    predictionDate: '2024-03-01',
    predictedEvent: 'تغيير الزيت',
    predictedMileage: 50000,
    predictedDate: '2024-05-15',
    probability: 0.85,
    confidenceLevel: 'high',
    recommendations: 'قم بتغيير الزيت خلال الـ 5000 كم القادمة',
    createdAt: '2024-03-01',
  },
  {
    id: 2,
    carId: 1,
    predictionType: 'failure',
    predictionDate: '2024-03-01',
    predictedEvent: 'تلف البطارية',
    predictedDate: '2024-06-01',
    probability: 0.65,
    confidenceLevel: 'medium',
    contributingFactors: 'عمر السيارة: 4 سنوات',
    recommendations: 'اختبر البطارية في الخدمة القادمة',
    createdAt: '2024-03-01',
  },
];

const mockDashboardStats: DashboardStats = {
  totalCars: 3,
  activeCars: 3,
  totalMaintenanceRecords: 16,
  totalFailures: 5,
  totalMaintenanceCost: 11500,
  totalRepairCost: 1700,
  grandTotalCost: 13200,
  averageMileage: 45000,
  averageMaintenanceCostPerCar: 3833,
  averageRepairCostPerCar: 567,
  monthlyCosts: [
    { year: 2024, month: 3, maintenanceCost: 1200, repairCost: 300, maintenanceCount: 3, failureCount: 1 },
    { year: 2024, month: 2, maintenanceCost: 800, repairCost: 0, maintenanceCount: 2, failureCount: 0 },
    { year: 2024, month: 1, maintenanceCost: 1500, repairCost: 500, maintenanceCount: 4, failureCount: 2 },
    { year: 2023, month: 12, maintenanceCost: 600, repairCost: 200, maintenanceCount: 2, failureCount: 1 },
    { year: 2023, month: 11, maintenanceCost: 1000, repairCost: 400, maintenanceCount: 3, failureCount: 1 },
  ],
  topFailures: [
    { failureTypeName: 'تسريب زيت', failureTypeNameAr: 'تسريب زيت', count: 3, totalCost: 450, averageCost: 150 },
    { failureTypeName: 'نفاد البطارية', failureTypeNameAr: 'نفاد البطارية', count: 2, totalCost: 600, averageCost: 300 },
    { failureTypeName: 'صوت الفرامل', failureTypeNameAr: 'صوت الفرامل', count: 2, totalCost: 400, averageCost: 200 },
  ],
  maintenanceStats: [
    { maintenanceTypeName: 'تغيير الزيت', count: 8, totalCost: 2800, averageCost: 350 },
    { maintenanceTypeName: 'تغيير تيل الفرامل', count: 3, totalCost: 2400, averageCost: 800 },
    { maintenanceTypeName: 'تغيير فلتر الهواء', count: 4, totalCost: 320, averageCost: 80 },
  ],
  healthDistribution: {
    excellent: 1,
    good: 1,
    fair: 1,
    poor: 0,
    critical: 0,
  },
  upcomingMaintenance: [
    {
      carId: 1,
      plateNumber: 'ABC-1234',
      brand: 'Toyota',
      model: 'Camry',
      maintenanceType: 'تغيير الزيت',
      dueMileage: 50000,
      kmUntilDue: 5000,
      priority: 'high',
    },
    {
      carId: 2,
      plateNumber: 'XYZ-5678',
      brand: 'Honda',
      model: 'Civic',
      maintenanceType: 'تغيير تيل الفرامل',
      dueDate: '2024-04-15',
      daysUntilDue: 30,
      priority: 'medium',
    },
  ],
};

export default function Home() {
  const { toast } = useToast();
  const [activeTab, setActiveTab] = useState('dashboard');
  const [cars, setCars] = useState<Car[]>(mockCars);
  const [selectedCar, setSelectedCar] = useState<Car | null>(null);
  const [dashboardStats, setDashboardStats] = useState<DashboardStats>(mockDashboardStats);
  const [maintenanceRecords, setMaintenanceRecords] = useState<MaintenanceRecord[]>(mockMaintenanceRecords);
  const [failures, setFailures] = useState<Failure[]>(mockFailures);
  const [predictions, setPredictions] = useState<Prediction[]>(mockPredictions);
  
  const [isCarDialogOpen, setIsCarDialogOpen] = useState(false);
  const [isMaintenanceDialogOpen, setIsMaintenanceDialogOpen] = useState(false);
  const [isFailureDialogOpen, setIsFailureDialogOpen] = useState(false);
  const [editingCar, setEditingCar] = useState<Car | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  // Refresh dashboard data
  const refreshDashboard = () => {
    setIsLoading(true);
    setTimeout(() => {
      setDashboardStats(mockDashboardStats);
      setIsLoading(false);
      toast({
        title: 'تم التحديث',
        description: 'تم تحديث البيانات بنجاح',
      });
    }, 1000);
  };

  // Car handlers
  const handleAddCar = () => {
    setEditingCar(null);
    setIsCarDialogOpen(true);
  };

  const handleEditCar = (car: Car) => {
    setEditingCar(car);
    setIsCarDialogOpen(true);
  };

  const handleDeleteCar = (id: number) => {
    setCars(cars.filter((c) => c.id !== id));
    if (selectedCar?.id === id) {
      setSelectedCar(null);
    }
    toast({
      title: 'تم الحذف',
      description: 'تم حذف السيارة بنجاح',
    });
  };

  const handleSelectCar = (car: Car) => {
    setSelectedCar(car);
    setActiveTab('cars');
  };

  const handleCarSubmit = (data: Partial<Car>) => {
    if (editingCar) {
      setCars(cars.map((c) => (c.id === editingCar.id ? { ...c, ...data } : c)));
      toast({
        title: 'تم التحديث',
        description: 'تم تحديث بيانات السيارة',
      });
    } else {
      const newCar: Car = {
        ...data,
        id: cars.length + 1,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        carAge: new Date().getFullYear() - (data.year || new Date().getFullYear()),
        healthScore: 100,
        maintenanceCount: 0,
        failureCount: 0,
        totalMaintenanceCost: 0,
        totalRepairCost: 0,
      } as Car;
      setCars([...cars, newCar]);
      toast({
        title: 'تم الإضافة',
        description: 'تم إضافة السيارة بنجاح',
      });
    }
  };

  // Maintenance handlers
  const handleAddMaintenance = () => {
    if (!selectedCar) return;
    setIsMaintenanceDialogOpen(true);
  };

  const handleMaintenanceSubmit = (data: Partial<MaintenanceRecord>) => {
    const newRecord: MaintenanceRecord = {
      ...data,
      id: maintenanceRecords.length + 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    } as MaintenanceRecord;
    setMaintenanceRecords([...maintenanceRecords, newRecord]);
    toast({
      title: 'تم الإضافة',
      description: 'تم إضافة سجل الصيانة بنجاح',
    });
  };

  // Failure handlers
  const handleAddFailure = () => {
    if (!selectedCar) return;
    setIsFailureDialogOpen(true);
  };

  const handleFailureSubmit = (data: Partial<Failure>) => {
    const newFailure: Failure = {
      ...data,
      id: failures.length + 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    } as Failure;
    setFailures([...failures, newFailure]);
    toast({
      title: 'تم التسجيل',
      description: 'تم تسجيل العطل بنجاح',
    });
  };

  // Predictions handler
  const handleGeneratePredictions = () => {
    if (!selectedCar) return;
    setIsLoading(true);
    setTimeout(() => {
      setPredictions(mockPredictions);
      setIsLoading(false);
      toast({
        title: 'تم توليد التوقعات',
        description: 'تم تحليل البيانات وتوليد التوقعات',
      });
    }, 1500);
  };

  return (
    <div className="min-h-screen bg-background" dir="rtl">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-lg bg-primary/10">
              <CarIcon className="h-6 w-6 text-primary" />
            </div>
            <div>
              <h1 className="text-lg font-bold">نظام صيانة السيارات</h1>
              <p className="text-xs text-muted-foreground">Smart Car Maintenance Analytics</p>
            </div>
          </div>
          <Button variant="outline" size="sm" onClick={refreshDashboard} disabled={isLoading}>
            <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
            تحديث
          </Button>
        </div>
      </header>

      {/* Main Content */}
      <main className="container px-4 py-6">
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full grid-cols-4 lg:w-[400px]">
            <TabsTrigger value="dashboard" className="gap-2">
              <LayoutDashboard className="h-4 w-4" />
              <span className="hidden sm:inline">لوحة التحكم</span>
            </TabsTrigger>
            <TabsTrigger value="cars" className="gap-2">
              <CarIcon className="h-4 w-4" />
              <span className="hidden sm:inline">السيارات</span>
            </TabsTrigger>
            <TabsTrigger value="maintenance" className="gap-2">
              <Wrench className="h-4 w-4" />
              <span className="hidden sm:inline">الصيانة</span>
            </TabsTrigger>
            <TabsTrigger value="failures" className="gap-2">
              <AlertTriangle className="h-4 w-4" />
              <span className="hidden sm:inline">الأعطال</span>
            </TabsTrigger>
          </TabsList>

          {/* Dashboard Tab */}
          <TabsContent value="dashboard" className="space-y-6">
            <StatsCards stats={dashboardStats} />
            
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              <div className="lg:col-span-2">
                <MonthlyCostChart data={dashboardStats.monthlyCosts} />
              </div>
              <HealthScoreCard healthDistribution={dashboardStats.healthDistribution} />
            </div>

            <div className="grid gap-6 md:grid-cols-2">
              <FailureStatsChart data={dashboardStats.topFailures} />
              <MaintenanceStatsChart data={dashboardStats.maintenanceStats} />
            </div>

            <UpcomingMaintenanceCard items={dashboardStats.upcomingMaintenance} />
          </TabsContent>

          {/* Cars Tab */}
          <TabsContent value="cars" className="space-y-6">
            <div className="grid gap-6 lg:grid-cols-3">
              <div className="lg:col-span-1">
                <CarsList
                  cars={cars}
                  onAddCar={handleAddCar}
                  onEditCar={handleEditCar}
                  onDeleteCar={handleDeleteCar}
                  onSelectCar={handleSelectCar}
                  selectedCarId={selectedCar?.id}
                />
              </div>
              <div className="lg:col-span-2">
                <CarDetails
                  car={selectedCar}
                  maintenanceRecords={selectedCar ? maintenanceRecords.filter(m => m.carId === selectedCar.id) : []}
                  failures={selectedCar ? failures.filter(f => f.carId === selectedCar.id) : []}
                  predictions={selectedCar ? predictions.filter(p => p.carId === selectedCar.id) : []}
                  onAddMaintenance={handleAddMaintenance}
                  onAddFailure={handleAddFailure}
                  onGeneratePredictions={handleGeneratePredictions}
                />
              </div>
            </div>
          </TabsContent>

          {/* Maintenance Tab */}
          <TabsContent value="maintenance" className="space-y-6">
            <div className="grid gap-6 lg:grid-cols-3">
              <div className="lg:col-span-2">
                <MonthlyCostChart data={dashboardStats.monthlyCosts} />
              </div>
              <UpcomingMaintenanceCard items={dashboardStats.upcomingMaintenance} />
            </div>
            <MaintenanceStatsChart data={dashboardStats.maintenanceStats} />
          </TabsContent>

          {/* Failures Tab */}
          <TabsContent value="failures" className="space-y-6">
            <div className="grid gap-6 md:grid-cols-2">
              <FailureStatsChart data={dashboardStats.topFailures} />
              <HealthScoreCard healthDistribution={dashboardStats.healthDistribution} />
            </div>
          </TabsContent>
        </Tabs>
      </main>

      {/* Dialogs */}
      <CarDialog
        open={isCarDialogOpen}
        onOpenChange={setIsCarDialogOpen}
        car={editingCar}
        onSubmit={handleCarSubmit}
      />

      <MaintenanceDialog
        open={isMaintenanceDialogOpen}
        onOpenChange={setIsMaintenanceDialogOpen}
        carId={selectedCar?.id || 0}
        onSubmit={handleMaintenanceSubmit}
      />

      <FailureDialog
        open={isFailureDialogOpen}
        onOpenChange={setIsFailureDialogOpen}
        carId={selectedCar?.id || 0}
        onSubmit={handleFailureSubmit}
      />

      <Toaster />
    </div>
  );
}
