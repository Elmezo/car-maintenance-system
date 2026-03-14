'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { 
  Car, 
  Wrench, 
  AlertTriangle, 
  DollarSign,
  TrendingUp,
  Activity
} from 'lucide-react';

interface StatsCardsProps {
  stats: {
    totalCars: number;
    activeCars: number;
    totalMaintenanceRecords: number;
    totalFailures: number;
    totalMaintenanceCost: number;
    totalRepairCost: number;
    grandTotalCost: number;
    averageMileage?: number;
    averageMaintenanceCostPerCar?: number;
  };
}

export function StatsCards({ stats }: StatsCardsProps) {
  const cards = [
    {
      title: 'إجمالي السيارات',
      value: stats.totalCars,
      subValue: `${stats.activeCars} نشطة`,
      icon: Car,
      color: 'text-blue-500',
      bgColor: 'bg-blue-500/10',
    },
    {
      title: 'سجلات الصيانة',
      value: stats.totalMaintenanceRecords,
      subValue: 'إجمالي العمليات',
      icon: Wrench,
      color: 'text-green-500',
      bgColor: 'bg-green-500/10',
    },
    {
      title: 'الأعطال',
      value: stats.totalFailures,
      subValue: 'إجمالي الأعطال',
      icon: AlertTriangle,
      color: 'text-orange-500',
      bgColor: 'bg-orange-500/10',
    },
    {
      title: 'إجمالي التكاليف',
      value: `${stats.grandTotalCost?.toLocaleString() || 0} ج.م`,
      subValue: `صيانة: ${stats.totalMaintenanceCost?.toLocaleString() || 0}`,
      icon: DollarSign,
      color: 'text-emerald-500',
      bgColor: 'bg-emerald-500/10',
    },
  ];

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      {cards.map((card, index) => (
        <Card key={index} className="overflow-hidden">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              {card.title}
            </CardTitle>
            <div className={`p-2 rounded-lg ${card.bgColor}`}>
              <card.icon className={`h-4 w-4 ${card.color}`} />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{card.value}</div>
            <p className="text-xs text-muted-foreground mt-1">{card.subValue}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

interface HealthScoreCardProps {
  healthDistribution: Record<string, number>;
}

export function HealthScoreCard({ healthDistribution }: HealthScoreCardProps) {
  const total = Object.values(healthDistribution).reduce((a, b) => a + b, 0);
  
  const healthLevels = [
    { key: 'excellent', label: 'ممتاز', color: 'bg-green-500' },
    { key: 'good', label: 'جيد', color: 'bg-blue-500' },
    { key: 'fair', label: 'متوسط', color: 'bg-yellow-500' },
    { key: 'poor', label: 'ضعيف', color: 'bg-orange-500' },
    { key: 'critical', label: 'حرج', color: 'bg-red-500' },
  ];

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Activity className="h-5 w-5 text-primary" />
          توزيع صحة السيارات
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {healthLevels.map((level) => {
            const count = healthDistribution[level.key] || 0;
            const percentage = total > 0 ? (count / total) * 100 : 0;
            
            return (
              <div key={level.key} className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="flex items-center gap-2">
                    <div className={`w-3 h-3 rounded-full ${level.color}`} />
                    {level.label}
                  </span>
                  <span className="text-muted-foreground">{count} سيارة</span>
                </div>
                <Progress value={percentage} className="h-2" />
              </div>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}
