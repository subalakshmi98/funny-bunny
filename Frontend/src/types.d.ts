export type TeacherStat = {
  teacherId: number;
  teacherName: string;
  sectionsAssigned: number;
  weeklyHours: number;
  utilizationPercent: number;
  dailyLoad: Record<string, number>;
};

export type RoomStat = {
  roomId: number;
  roomName: string;
  weeklyHoursUsed: number;
  utilizationPercent: number;
  dailyLoad: Record<string, number>;
  overlapCount: number;
};
