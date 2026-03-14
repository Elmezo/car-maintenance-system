'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from '@/components/ui/chart';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  ResponsiveContainer,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts';
import { TrendingUp, PieChartIcon } from 'lucide-react';
import type { MonthlyCost, FailureStats, MaintenanceStats } from '@/types';

interface MonthlyCostChartProps {
  data: MonthlyCost[];
}

const chartConfig = {
  maintenanceCost: {
    label: 'تكلفة الصيانة',
    color: 'hsl(var(--chart-1))',
  },
  repairCost: {
    label: 'تكلفة الإصلاح',
    color: 'hsl(var(--chart-2))',
  },
} satisfies ChartConfig;

export function MonthlyCostChart({ data }: MonthlyCostChartProps) {
  const chartData = data.map((item) => ({
    month: `${item.month}/${item.year}`,
    maintenanceCost: item.maintenanceCost,
    repairCost: item.repairCost,
  }));

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <TrendingUp className="h-5 w-5 text-primary" />
          التكاليف الشهرية
        </CardTitle>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig} className="h-[300px]">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
              <XAxis dataKey="month" className="text-xs" />
              <YAxis className="text-xs" />
              <ChartTooltip content={<ChartTooltipContent />} />
              <Legend />
              <Bar
                dataKey="maintenanceCost"
                fill="var(--color-maintenanceCost)"
                name="تكلفة الصيانة"
                radius={[4, 4, 0, 0]}
              />
              <Bar
                dataKey="repairCost"
                fill="var(--color-repairCost)"
                name="تكلفة الإصلاح"
                radius={[4, 4, 0, 0]}
              />
            </BarChart>
          </ResponsiveContainer>
        </ChartContainer>
      </CardContent>
    </Card>
  );
}

interface FailureStatsChartProps {
  data: FailureStats[];
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82CA9D'];

export function FailureStatsChart({ data }: FailureStatsChartProps) {
  const chartData = data.slice(0, 6).map((item) => ({
    name: item.failureTypeName,
    value: item.count,
    nameAr: item.failureTypeNameAr,
  }));

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <PieChartIcon className="h-5 w-5 text-primary" />
          أكثر الأعطال تكراراً
        </CardTitle>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig} className="h-[300px]">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={chartData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} (${(percent * 100).toFixed(0)}%)`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <ChartTooltip content={<ChartTooltipContent />} />
            </PieChart>
          </ResponsiveContainer>
        </ChartContainer>
      </CardContent>
    </Card>
  );
}

interface MaintenanceStatsChartProps {
  data: MaintenanceStats[];
}

export function MaintenanceStatsChart({ data }: MaintenanceStatsChartProps) {
  const chartData = data.slice(0, 6).map((item) => ({
    name: item.maintenanceTypeName,
    count: item.count,
    totalCost: item.totalCost,
    nameAr: item.maintenanceTypeNameAr,
  }));

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <TrendingUp className="h-5 w-5 text-primary" />
          أنواع الصيانة
        </CardTitle>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig} className="h-[300px]">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
              <XAxis dataKey="name" className="text-xs" angle={-45} textAnchor="end" height={60} />
              <YAxis className="text-xs" />
              <ChartTooltip content={<ChartTooltipContent />} />
              <Legend />
              <Line
                type="monotone"
                dataKey="count"
                stroke="var(--color-maintenanceCost)"
                name="العدد"
                strokeWidth={2}
              />
              <Line
                type="monotone"
                dataKey="totalCost"
                stroke="var(--color-repairCost)"
                name="التكلفة الإجمالية"
                strokeWidth={2}
              />
            </LineChart>
          </ResponsiveContainer>
        </ChartContainer>
      </CardContent>
    </Card>
  );
}
