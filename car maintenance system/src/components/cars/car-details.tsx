'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Progress } from '@/components/ui/progress';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  Car as CarIcon,
  Gauge,
  Calendar,
  Settings,
  User,
  Phone,
  Mail,
  Wrench,
  AlertTriangle,
  TrendingUp,
  DollarSign,
  Activity,
  Battery,
  Fuel,
  Clock,
} from 'lucide-react';
import type { Car, MaintenanceRecord, Failure, Prediction } from '@/types';

interface CarDetailsProps {
  car: Car | null;
  maintenanceRecords: MaintenanceRecord[];
  failures: Failure[];
  predictions: Prediction[];
  onAddMaintenance: () => void;
  onAddFailure: () => void;
  onGeneratePredictions: () => void;
}

export function CarDetails({
  car,
  maintenanceRecords,
  failures,
  predictions,
  onAddMaintenance,
  onAddFailure,
  onGeneratePredictions,
}: CarDetailsProps) {
  if (!car) {
    return (
      <Card className="h-full flex items-center justify-center">
        <CardContent className="text-center text-muted-foreground">
          <CarIcon className="h-16 w-16 mx-auto mb-4 opacity-50" />
          <p className="text-lg">اختر سيارة لعرض التفاصيل</p>
        </CardContent>
      </Card>
    );
  }

  const getHealthLabel = (score?: number) => {
    if (!score) return { label: 'غير محدد', color: 'text-gray-400' };
    if (score >= 90) return { label: 'ممتاز', color: 'text-green-500' };
    if (score >= 75) return { label: 'جيد', color: 'text-blue-500' };
    if (score >= 50) return { label: 'متوسط', color: 'text-yellow-500' };
    if (score >= 25) return { label: 'ضعيف', color: 'text-orange-500' };
    return { label: 'حرج', color: 'text-red-500' };
  };

  const healthInfo = getHealthLabel(car.healthScore);

  const getEngineTypeLabel = (type: string) => {
    switch (type) {
      case 'petrol':
        return 'بنزين';
      case 'diesel':
        return 'ديزل';
      case 'electric':
        return 'كهرباء';
      case 'hybrid':
        return 'هايبرد';
      default:
        return type;
    }
  };

  const getTransmissionLabel = (trans: string) => {
    switch (trans) {
      case 'manual':
        return 'مانيوال';
      case 'automatic':
        return 'أوتوماتيك';
      case 'cvt':
        return 'CVT';
      default:
        return trans;
    }
  };

  const getSeverityBadge = (severity: string) => {
    switch (severity) {
      case 'minor':
        return <Badge variant="secondary">بسيط</Badge>;
      case 'moderate':
        return <Badge className="bg-yellow-500">متوسط</Badge>;
      case 'major':
        return <Badge className="bg-orange-500">كبير</Badge>;
      case 'critical':
        return <Badge variant="destructive">حرج</Badge>;
      default:
        return <Badge variant="secondary">{severity}</Badge>;
    }
  };

  return (
    <Card className="h-full">
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <CarIcon className="h-5 w-5 text-primary" />
            {car.brand} {car.model} ({car.year})
          </CardTitle>
          <div className="flex gap-2">
            <Button size="sm" variant="outline" onClick={onAddMaintenance}>
              <Wrench className="h-4 w-4 mr-1" />
              صيانة
            </Button>
            <Button size="sm" variant="outline" onClick={onAddFailure}>
              <AlertTriangle className="h-4 w-4 mr-1" />
              عطل
            </Button>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue="overview" className="w-full">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="overview">نظرة عامة</TabsTrigger>
            <TabsTrigger value="maintenance">الصيانة</TabsTrigger>
            <TabsTrigger value="failures">الأعطال</TabsTrigger>
            <TabsTrigger value="predictions">التوقعات</TabsTrigger>
          </TabsList>

          <TabsContent value="overview" className="mt-4 space-y-4">
            {/* Health Score */}
            <div className="flex items-center justify-between p-4 rounded-lg bg-accent/50">
              <div>
                <p className="text-sm text-muted-foreground">صحة السيارة</p>
                <p className={`text-2xl font-bold ${healthInfo.color}`}>
                  {car.healthScore || '-'}% - {healthInfo.label}
                </p>
              </div>
              <Activity className="h-8 w-8 text-primary" />
            </div>

            {/* Car Info */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-3">
                <div className="flex items-center gap-2 text-sm">
                  <Gauge className="h-4 w-4 text-muted-foreground" />
                  <span>{car.currentMileage?.toLocaleString()} كم</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Fuel className="h-4 w-4 text-muted-foreground" />
                  <span>{getEngineTypeLabel(car.engineType)}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Settings className="h-4 w-4 text-muted-foreground" />
                  <span>{getTransmissionLabel(car.transmission)}</span>
                </div>
              </div>
              <div className="space-y-3">
                <div className="flex items-center gap-2 text-sm">
                  <Calendar className="h-4 w-4 text-muted-foreground" />
                  <span>عمر السيارة: {car.carAge || car.year ? new Date().getFullYear() - car.year : 0} سنة</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Battery className="h-4 w-4 text-muted-foreground" />
                  <span>البطارية: {car.vin?.slice(0, 8) || '-'}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Clock className="h-4 w-4 text-muted-foreground" />
                  <span>رقم اللوحة: {car.plateNumber}</span>
                </div>
              </div>
            </div>

            {/* Owner Info */}
            {car.ownerName && (
              <div className="p-3 rounded-lg border">
                <p className="text-sm font-medium mb-2">معلومات المالك</p>
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-sm">
                    <User className="h-4 w-4 text-muted-foreground" />
                    <span>{car.ownerName}</span>
                  </div>
                  {car.ownerPhone && (
                    <div className="flex items-center gap-2 text-sm">
                      <Phone className="h-4 w-4 text-muted-foreground" />
                      <span>{car.ownerPhone}</span>
                    </div>
                  )}
                  {car.ownerEmail && (
                    <div className="flex items-center gap-2 text-sm">
                      <Mail className="h-4 w-4 text-muted-foreground" />
                      <span>{car.ownerEmail}</span>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Stats */}
            <div className="grid grid-cols-3 gap-3">
              <div className="p-3 rounded-lg bg-green-500/10 text-center">
                <p className="text-2xl font-bold text-green-500">
                  {car.maintenanceCount || maintenanceRecords.length}
                </p>
                <p className="text-xs text-muted-foreground">صيانة</p>
              </div>
              <div className="p-3 rounded-lg bg-orange-500/10 text-center">
                <p className="text-2xl font-bold text-orange-500">
                  {car.failureCount || failures.length}
                </p>
                <p className="text-xs text-muted-foreground">عطل</p>
              </div>
              <div className="p-3 rounded-lg bg-blue-500/10 text-center">
                <p className="text-lg font-bold text-blue-500">
                  {(car.totalMaintenanceCost || 0) + (car.totalRepairCost || 0)}
                </p>
                <p className="text-xs text-muted-foreground">ج.م تكلفة</p>
              </div>
            </div>
          </TabsContent>

          <TabsContent value="maintenance" className="mt-4">
            <ScrollArea className="h-[400px]">
              <div className="space-y-2">
                {maintenanceRecords.length === 0 ? (
                  <div className="text-center text-muted-foreground py-8">
                    <Wrench className="h-12 w-12 mx-auto mb-2 opacity-50" />
                    <p>لا توجد سجلات صيانة</p>
                  </div>
                ) : (
                  maintenanceRecords.map((record) => (
                    <div
                      key={record.id}
                      className="p-3 rounded-lg border hover:bg-accent/50 transition-colors"
                    >
                      <div className="flex items-start justify-between">
                        <div>
                          <p className="font-medium">{record.maintenanceTypeName}</p>
                          <p className="text-sm text-muted-foreground">
                            {new Date(record.serviceDate).toLocaleDateString('ar-EG')}
                          </p>
                          <p className="text-xs text-muted-foreground">
                            {record.mileageAtService?.toLocaleString()} كم
                          </p>
                        </div>
                        <div className="text-left">
                          <p className="font-bold text-green-500">
                            {record.cost?.toLocaleString()} ج.م
                          </p>
                          {record.workshopName && (
                            <p className="text-xs text-muted-foreground">
                              {record.workshopName}
                            </p>
                          )}
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </ScrollArea>
          </TabsContent>

          <TabsContent value="failures" className="mt-4">
            <ScrollArea className="h-[400px]">
              <div className="space-y-2">
                {failures.length === 0 ? (
                  <div className="text-center text-muted-foreground py-8">
                    <AlertTriangle className="h-12 w-12 mx-auto mb-2 opacity-50" />
                    <p>لا توجد أعطال مسجلة</p>
                  </div>
                ) : (
                  failures.map((failure) => (
                    <div
                      key={failure.id}
                      className="p-3 rounded-lg border hover:bg-accent/50 transition-colors"
                    >
                      <div className="flex items-start justify-between">
                        <div>
                          <p className="font-medium">{failure.failureTypeName}</p>
                          <p className="text-sm text-muted-foreground">
                            {new Date(failure.failureDate).toLocaleDateString('ar-EG')}
                          </p>
                          {failure.description && (
                            <p className="text-xs text-muted-foreground mt-1">
                              {failure.description}
                            </p>
                          )}
                        </div>
                        <div className="flex flex-col items-end gap-2">
                          {getSeverityBadge(failure.severity)}
                          {failure.isRepaired && (
                            <Badge className="bg-green-500">تم الإصلاح</Badge>
                          )}
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </ScrollArea>
          </TabsContent>

          <TabsContent value="predictions" className="mt-4">
            <div className="mb-4">
              <Button onClick={onGeneratePredictions} className="w-full">
                <TrendingUp className="h-4 w-4 mr-2" />
                توليد توقعات جديدة
              </Button>
            </div>
            <ScrollArea className="h-[350px]">
              <div className="space-y-2">
                {predictions.length === 0 ? (
                  <div className="text-center text-muted-foreground py-8">
                    <TrendingUp className="h-12 w-12 mx-auto mb-2 opacity-50" />
                    <p>لا توجد توقعات</p>
                    <p className="text-sm">اضغط على الزر أعلاه لتوليد توقعات</p>
                  </div>
                ) : (
                  predictions.map((prediction, index) => (
                    <div
                      key={prediction.id || index}
                      className="p-3 rounded-lg border hover:bg-accent/50 transition-colors"
                    >
                      <div className="flex items-start justify-between">
                        <div>
                          <p className="font-medium">{prediction.predictedEvent}</p>
                          {prediction.predictedDate && (
                            <p className="text-sm text-muted-foreground">
                              التاريخ المتوقع: {new Date(prediction.predictedDate).toLocaleDateString('ar-EG')}
                            </p>
                          )}
                          {prediction.predictedMileage && (
                            <p className="text-sm text-muted-foreground">
                              الكيلومتر المتوقع: {prediction.predictedMileage?.toLocaleString()} كم
                            </p>
                          )}
                          {prediction.recommendations && (
                            <p className="text-xs text-muted-foreground mt-1">
                              {prediction.recommendations}
                            </p>
                          )}
                        </div>
                        <div className="flex flex-col items-end gap-2">
                          <Badge
                            className={
                              prediction.predictionType === 'failure'
                                ? 'bg-red-500'
                                : prediction.predictionType === 'maintenance'
                                ? 'bg-blue-500'
                                : 'bg-green-500'
                            }
                          >
                            {prediction.predictionType === 'failure'
                              ? 'عطل'
                              : prediction.predictionType === 'maintenance'
                              ? 'صيانة'
                              : 'تكلفة'}
                          </Badge>
                          {prediction.probability && (
                            <span className="text-xs text-muted-foreground">
                              {(prediction.probability * 100).toFixed(0)}% احتمالية
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </ScrollArea>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  );
}
