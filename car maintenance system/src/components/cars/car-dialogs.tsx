'use client';

import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Textarea } from '@/components/ui/textarea';
import type { Car, MaintenanceRecord, Failure } from '@/types';

interface CarDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  car?: Car | null;
  onSubmit: (data: Partial<Car>) => void;
}

export function CarDialog({ open, onOpenChange, car, onSubmit }: CarDialogProps) {
  const [formData, setFormData] = useState<Partial<Car>>(
    car || {
      plateNumber: '',
      brand: '',
      model: '',
      year: new Date().getFullYear(),
      color: '',
      vin: '',
      currentMileage: 0,
      engineType: 'petrol',
      transmission: 'automatic',
      ownerName: '',
      ownerPhone: '',
      ownerEmail: '',
      status: 'active',
      notes: '',
    }
  );

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{car ? 'تعديل السيارة' : 'إضافة سيارة جديدة'}</DialogTitle>
          <DialogDescription>
            أدخل بيانات السيارة كاملة
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="plateNumber">رقم اللوحة *</Label>
              <Input
                id="plateNumber"
                value={formData.plateNumber}
                onChange={(e) => setFormData({ ...formData, plateNumber: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="year">سنة الصنع *</Label>
              <Input
                id="year"
                type="number"
                value={formData.year}
                onChange={(e) => setFormData({ ...formData, year: parseInt(e.target.value) })}
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="brand">الماركة *</Label>
              <Input
                id="brand"
                value={formData.brand}
                onChange={(e) => setFormData({ ...formData, brand: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="model">الموديل *</Label>
              <Input
                id="model"
                value={formData.model}
                onChange={(e) => setFormData({ ...formData, model: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="color">اللون</Label>
              <Input
                id="color"
                value={formData.color}
                onChange={(e) => setFormData({ ...formData, color: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="vin">رقم الشاصي</Label>
              <Input
                id="vin"
                value={formData.vin}
                onChange={(e) => setFormData({ ...formData, vin: e.target.value })}
                maxLength={17}
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div className="space-y-2">
              <Label htmlFor="currentMileage">العداد الحالي (كم)</Label>
              <Input
                id="currentMileage"
                type="number"
                value={formData.currentMileage}
                onChange={(e) => setFormData({ ...formData, currentMileage: parseInt(e.target.value) })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="engineType">نوع المحرك</Label>
              <Select
                value={formData.engineType}
                onValueChange={(value: any) => setFormData({ ...formData, engineType: value })}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="petrol">بنزين</SelectItem>
                  <SelectItem value="diesel">ديزل</SelectItem>
                  <SelectItem value="electric">كهرباء</SelectItem>
                  <SelectItem value="hybrid">هايبرد</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="transmission">القير</Label>
              <Select
                value={formData.transmission}
                onValueChange={(value: any) => setFormData({ ...formData, transmission: value })}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="manual">مانيوال</SelectItem>
                  <SelectItem value="automatic">أوتوماتيك</SelectItem>
                  <SelectItem value="cvt">CVT</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="ownerName">اسم المالك</Label>
              <Input
                id="ownerName"
                value={formData.ownerName}
                onChange={(e) => setFormData({ ...formData, ownerName: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="ownerPhone">رقم الهاتف</Label>
              <Input
                id="ownerPhone"
                value={formData.ownerPhone}
                onChange={(e) => setFormData({ ...formData, ownerPhone: e.target.value })}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="ownerEmail">البريد الإلكتروني</Label>
            <Input
              id="ownerEmail"
              type="email"
              value={formData.ownerEmail}
              onChange={(e) => setFormData({ ...formData, ownerEmail: e.target.value })}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="status">الحالة</Label>
            <Select
              value={formData.status}
              onValueChange={(value: any) => setFormData({ ...formData, status: value })}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="active">نشط</SelectItem>
                <SelectItem value="inactive">غير نشط</SelectItem>
                <SelectItem value="sold">مباع</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="notes">ملاحظات</Label>
            <Textarea
              id="notes"
              value={formData.notes}
              onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
              rows={3}
            />
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              إلغاء
            </Button>
            <Button type="submit">{car ? 'حفظ التغييرات' : 'إضافة السيارة'}</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

interface MaintenanceDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  carId: number;
  onSubmit: (data: Partial<MaintenanceRecord>) => void;
}

export function MaintenanceDialog({ open, onOpenChange, carId, onSubmit }: MaintenanceDialogProps) {
  const [formData, setFormData] = useState<Partial<MaintenanceRecord>>({
    carId,
    maintenanceTypeId: 1,
    serviceDate: new Date().toISOString().split('T')[0],
    mileageAtService: 0,
    cost: 0,
    workshopName: '',
    description: '',
    status: 'completed',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ ...formData, carId });
    onOpenChange(false);
  };

  const maintenanceTypes = [
    { id: 1, name: 'تغيير الزيت' },
    { id: 2, name: 'تغيير فلتر الزيت' },
    { id: 3, name: 'تغيير فلتر الهواء' },
    { id: 4, name: 'تغيير فلتر المكيف' },
    { id: 5, name: 'تغيير شمعات الإشعال' },
    { id: 6, name: 'تغيير تيل الفرامل' },
    { id: 7, name: 'تغيير زيت الفرامل' },
    { id: 8, name: 'تغيير سائل التبريد' },
    { id: 9, name: 'تغيير زيت القير' },
    { id: 10, name: 'تغيير سير التايمينج' },
    { id: 11, name: 'تدوير الإطارات' },
    { id: 12, name: 'تغيير الإطارات' },
    { id: 13, name: 'تغيير البطارية' },
    { id: 14, name: 'ضبط الزوايا' },
    { id: 15, name: 'صيانة المكيف' },
  ];

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>إضافة سجل صيانة</DialogTitle>
          <DialogDescription>أدخل بيانات عملية الصيانة</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="maintenanceType">نوع الصيانة</Label>
            <Select
              value={formData.maintenanceTypeId?.toString()}
              onValueChange={(value) => setFormData({ ...formData, maintenanceTypeId: parseInt(value) })}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {maintenanceTypes.map((type) => (
                  <SelectItem key={type.id} value={type.id.toString()}>
                    {type.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="serviceDate">تاريخ الخدمة</Label>
              <Input
                id="serviceDate"
                type="date"
                value={formData.serviceDate}
                onChange={(e) => setFormData({ ...formData, serviceDate: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="mileageAtService">العداد عند الخدمة (كم)</Label>
              <Input
                id="mileageAtService"
                type="number"
                value={formData.mileageAtService}
                onChange={(e) => setFormData({ ...formData, mileageAtService: parseInt(e.target.value) })}
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="cost">التكلفة (ج.م)</Label>
              <Input
                id="cost"
                type="number"
                value={formData.cost}
                onChange={(e) => setFormData({ ...formData, cost: parseFloat(e.target.value) })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="workshopName">اسم الورشة</Label>
              <Input
                id="workshopName"
                value={formData.workshopName}
                onChange={(e) => setFormData({ ...formData, workshopName: e.target.value })}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">الوصف</Label>
            <Textarea
              id="description"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              rows={2}
            />
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              إلغاء
            </Button>
            <Button type="submit">إضافة السجل</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

interface FailureDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  carId: number;
  onSubmit: (data: Partial<Failure>) => void;
}

export function FailureDialog({ open, onOpenChange, carId, onSubmit }: FailureDialogProps) {
  const [formData, setFormData] = useState<Partial<Failure>>({
    carId,
    failureTypeId: 1,
    failureDate: new Date().toISOString().split('T')[0],
    mileageAtFailure: 0,
    severity: 'moderate',
    description: '',
    symptoms: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ ...formData, carId });
    onOpenChange(false);
  };

  const failureTypes = [
    { id: 1, name: 'سخونة المحرك' },
    { id: 2, name: 'احتراق غير منتظم' },
    { id: 3, name: 'تسريب زيت' },
    { id: 4, name: 'تعطل الفرامل' },
    { id: 5, name: 'صوت الفرامل' },
    { id: 6, name: 'نفاد البطارية' },
    { id: 7, name: 'تعطل الدينامو' },
    { id: 8, name: 'تعطل موتور التشغيل' },
    { id: 9, name: 'انزلاق القير' },
    { id: 10, name: 'تعطل الدبرياج' },
    { id: 11, name: 'انفجار الإطار' },
    { id: 12, name: 'تآكل الإطارات' },
    { id: 13, name: 'تعطل المكيف' },
    { id: 14, name: 'صوت المساعدات' },
    { id: 15, name: 'مشاكل التوجيه' },
  ];

  const severityOptions = [
    { value: 'minor', label: 'بسيط' },
    { value: 'moderate', label: 'متوسط' },
    { value: 'major', label: 'كبير' },
    { value: 'critical', label: 'حرج' },
  ];

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>تسجيل عطل جديد</DialogTitle>
          <DialogDescription>أدخل بيانات العطل</DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="failureType">نوع العطل</Label>
            <Select
              value={formData.failureTypeId?.toString()}
              onValueChange={(value) => setFormData({ ...formData, failureTypeId: parseInt(value) })}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {failureTypes.map((type) => (
                  <SelectItem key={type.id} value={type.id.toString()}>
                    {type.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="failureDate">تاريخ العطل</Label>
              <Input
                id="failureDate"
                type="date"
                value={formData.failureDate}
                onChange={(e) => setFormData({ ...formData, failureDate: e.target.value })}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="mileageAtFailure">العداد عند العطل (كم)</Label>
              <Input
                id="mileageAtFailure"
                type="number"
                value={formData.mileageAtFailure}
                onChange={(e) => setFormData({ ...formData, mileageAtFailure: parseInt(e.target.value) })}
                required
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="severity">شدة العطل</Label>
            <Select
              value={formData.severity}
              onValueChange={(value: any) => setFormData({ ...formData, severity: value })}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {severityOptions.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">الوصف</Label>
            <Textarea
              id="description"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              rows={2}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="symptoms">الأعراض</Label>
            <Textarea
              id="symptoms"
              value={formData.symptoms}
              onChange={(e) => setFormData({ ...formData, symptoms: e.target.value })}
              rows={2}
            />
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              إلغاء
            </Button>
            <Button type="submit">تسجيل العطل</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
