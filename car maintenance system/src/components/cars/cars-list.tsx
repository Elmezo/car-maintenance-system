'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { ScrollArea } from '@/components/ui/scroll-area';
import { 
  Plus, 
  Search, 
  Car as CarIcon,
  Gauge,
  Calendar,
  Settings,
  Edit,
  Trash2
} from 'lucide-react';
import type { Car } from '@/types';

interface CarsListProps {
  cars: Car[];
  onAddCar: () => void;
  onEditCar: (car: Car) => void;
  onDeleteCar: (id: number) => void;
  onSelectCar: (car: Car) => void;
  selectedCarId?: number;
}

export function CarsList({ 
  cars, 
  onAddCar, 
  onEditCar, 
  onDeleteCar, 
  onSelectCar,
  selectedCarId 
}: CarsListProps) {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredCars = cars.filter(
    (car) =>
      car.plateNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      car.brand.toLowerCase().includes(searchTerm.toLowerCase()) ||
      car.model.toLowerCase().includes(searchTerm.toLowerCase()) ||
      car.ownerName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'active':
        return <Badge className="bg-green-500">نشط</Badge>;
      case 'inactive':
        return <Badge variant="secondary">غير نشط</Badge>;
      case 'sold':
        return <Badge variant="destructive">مباع</Badge>;
      default:
        return <Badge variant="secondary">{status}</Badge>;
    }
  };

  const getHealthColor = (score?: number) => {
    if (!score) return 'text-gray-400';
    if (score >= 90) return 'text-green-500';
    if (score >= 75) return 'text-blue-500';
    if (score >= 50) return 'text-yellow-500';
    if (score >= 25) return 'text-orange-500';
    return 'text-red-500';
  };

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

  return (
    <Card className="h-full">
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <CarIcon className="h-5 w-5 text-primary" />
            السيارات ({cars.length})
          </CardTitle>
          <Button size="sm" onClick={onAddCar}>
            <Plus className="h-4 w-4 mr-1" />
            إضافة
          </Button>
        </div>
        <div className="relative mt-2">
          <Search className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="بحث..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pr-9"
          />
        </div>
      </CardHeader>
      <CardContent className="p-0">
        <ScrollArea className="h-[calc(100vh-350px)]">
          <div className="space-y-2 p-4 pt-0">
            {filteredCars.length === 0 ? (
              <div className="text-center text-muted-foreground py-8">
                <CarIcon className="h-12 w-12 mx-auto mb-2 opacity-50" />
                <p>لا توجد سيارات</p>
              </div>
            ) : (
              filteredCars.map((car) => (
                <div
                  key={car.id}
                  className={`p-3 rounded-lg border cursor-pointer transition-colors ${
                    selectedCarId === car.id
                      ? 'border-primary bg-primary/5'
                      : 'hover:bg-accent/50'
                  }`}
                  onClick={() => onSelectCar(car)}
                >
                  <div className="flex items-start justify-between">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">
                          {car.brand} {car.model}
                        </span>
                        {getStatusBadge(car.status)}
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {car.plateNumber}
                      </p>
                      <div className="flex items-center gap-4 text-xs text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Calendar className="h-3 w-3" />
                          {car.year}
                        </span>
                        <span className="flex items-center gap-1">
                          <Gauge className="h-3 w-3" />
                          {car.currentMileage?.toLocaleString()} كم
                        </span>
                        <span className="flex items-center gap-1">
                          <Settings className="h-3 w-3" />
                          {getEngineTypeLabel(car.engineType)}
                        </span>
                      </div>
                    </div>
                    <div className="flex flex-col items-end gap-2">
                      <span className={`text-lg font-bold ${getHealthColor(car.healthScore)}`}>
                        {car.healthScore || '-'}
                      </span>
                      <div className="flex gap-1">
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-7 w-7"
                          onClick={(e) => {
                            e.stopPropagation();
                            onEditCar(car);
                          }}
                        >
                          <Edit className="h-3 w-3" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-7 w-7 text-destructive"
                          onClick={(e) => {
                            e.stopPropagation();
                            onDeleteCar(car.id);
                          }}
                        >
                          <Trash2 className="h-3 w-3" />
                        </Button>
                      </div>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </ScrollArea>
      </CardContent>
    </Card>
  );
}
