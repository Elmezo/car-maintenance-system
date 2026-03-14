'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ScrollArea } from '@/components/ui/scroll-area';
import { 
  Clock, 
  AlertCircle,
  Calendar,
  Gauge
} from 'lucide-react';
import type { UpcomingMaintenance } from '@/types';

interface UpcomingMaintenanceCardProps {
  items: UpcomingMaintenance[];
}

export function UpcomingMaintenanceCard({ items }: UpcomingMaintenanceCardProps) {
  const getPriorityColor = (priority: string) => {
    switch (priority.toLowerCase()) {
      case 'critical':
        return 'destructive';
      case 'high':
        return 'destructive';
      case 'medium':
        return 'warning';
      case 'low':
        return 'secondary';
      default:
        return 'secondary';
    }
  };

  const getPriorityLabel = (priority: string) => {
    switch (priority.toLowerCase()) {
      case 'critical':
        return 'حرج';
      case 'high':
        return 'عالي';
      case 'medium':
        return 'متوسط';
      case 'low':
        return 'منخفض';
      default:
        return priority;
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Clock className="h-5 w-5 text-primary" />
          الصيانة القادمة
        </CardTitle>
      </CardHeader>
      <CardContent>
        <ScrollArea className="h-[300px]">
          <div className="space-y-3">
            {items.length === 0 ? (
              <div className="text-center text-muted-foreground py-8">
                <AlertCircle className="h-8 w-8 mx-auto mb-2 opacity-50" />
                <p>لا توجد صيانات قادمة</p>
              </div>
            ) : (
              items.map((item, index) => (
                <div
                  key={index}
                  className="flex items-start justify-between p-3 rounded-lg border bg-card hover:bg-accent/50 transition-colors"
                >
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <span className="font-medium">
                        {item.brand} {item.model}
                      </span>
                      <span className="text-muted-foreground text-sm">
                        ({item.plateNumber})
                      </span>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      {item.maintenanceType}
                    </p>
                    <div className="flex items-center gap-4 text-xs text-muted-foreground">
                      {item.daysUntilDue !== undefined && (
                        <span className="flex items-center gap-1">
                          <Calendar className="h-3 w-3" />
                          {item.daysUntilDue <= 0 ? 'متأخر' : `${item.daysUntilDue} يوم`}
                        </span>
                      )}
                      {item.kmUntilDue !== undefined && (
                        <span className="flex items-center gap-1">
                          <Gauge className="h-3 w-3" />
                          {item.kmUntilDue <= 0 ? 'تجاوز الحد' : `${item.kmUntilDue} كم`}
                        </span>
                      )}
                    </div>
                  </div>
                  <Badge variant={getPriorityColor(item.priority) as any}>
                    {getPriorityLabel(item.priority)}
                  </Badge>
                </div>
              ))
            )}
          </div>
        </ScrollArea>
      </CardContent>
    </Card>
  );
}
