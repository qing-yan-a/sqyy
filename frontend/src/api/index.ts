import { get, post, put, remove } from './http'
import type {
  AdminUser,
  Drug,
  HealthNewsItem,
  InventoryLog,
  LoginResult,
  Patient,
  PharmacyVisitDetail,
  SearchResult,
  TimelineResult,
  UserProfile,
  VisitDetail,
} from '../types'

export const api = {
  login: (payload: { username: string; password: string }) =>
    post<LoginResult>('/auth/login', payload),
  getProfile: () => get<UserProfile>('/auth/profile'),

  getPatients: () => get<Patient[]>('/patients'),
  searchPatientsPage: (params?: { q?: string; page?: number; size?: number }) =>
    get<{ list: Patient[]; total: number; page: number; size: number }>('/patients', { params }),
  createPatient: (payload: Record<string, unknown>) =>
    post<Patient>('/patients', payload),
  updatePatient: (patientId: number, payload: Record<string, unknown>) =>
    put<Patient>(`/patients/${patientId}`, payload),
  deletePatient: (patientId: number) => remove<void>(`/patients/${patientId}`),

  getVisits: (patientId?: number) =>
    get<VisitDetail[]>('/visits', { params: patientId ? { patientId } : undefined }),
  searchVisitsPage: (params?: { q?: string; page?: number; size?: number }) =>
    get<{ list: VisitDetail[]; total: number; page: number; size: number }>('/visits', { params }),
  getPendingVisits: (doctorName: string) =>
    get<VisitDetail[]>('/visits/pending', { params: { doctorName } }),
  getDepartments: () =>
    get<string[]>('/visits/departments'),
  getDoctors: (department?: string) =>
    get<{ id: number; realName: string; username: string; department: string }[]>('/visits/doctors', {
      params: department ? { department } : undefined,
    }),
  register: (payload: { patientId: number; doctorName: string }) =>
    post<VisitDetail>('/visits/register', payload),
  cancelVisit: (visitId: number) =>
    post<void>(`/visits/${visitId}/cancel`),
  createVisit: (payload: Record<string, unknown>) =>
    post<VisitDetail>('/visits', payload),
  updateVisit: (visitId: number, payload: Record<string, unknown>) =>
    put<VisitDetail>(`/visits/${visitId}`, payload),
  deleteVisit: (visitId: number) => remove<void>(`/visits/${visitId}`),

  getDrugs: () => get<Drug[]>('/drugs'),
  searchDrugs: (params?: { q?: string; page?: number; size?: number; drugType?: string }) =>
    get<{ list: Drug[]; total: number; page: number; size: number }>('/drugs', { params }),
  createDrug: (payload: Record<string, unknown>) =>
    post<Drug>('/drugs', payload),
  deleteDrug: (drugId: number) => remove<void>(`/drugs/${drugId}`),
  updateDrugWarningStock: (drugId: number, warningStock: number) =>
    put<void>(`/drugs/${drugId}/warning-stock`, { warningStock }),
  getInventoryLogs: () => get<InventoryLog[]>('/drugs/inventory/logs'),
  searchInventoryLogs: (params: { page: number; size: number; q?: string }) =>
    get<{ list: InventoryLog[]; total: number; page: number; size: number }>('/drugs/inventory/logs', { params }),
  adjustStock: (payload: Record<string, unknown>) =>
    post<InventoryLog>('/drugs/inventory/logs', payload),

  getPickupTodos: () => get<PharmacyVisitDetail[]>('/pharmacy/pickup-todos'),
  getInjectionTodos: () => get<PharmacyVisitDetail[]>('/pharmacy/injection-todos'),
  getPharmacyVisitDetail: (visitId: number) =>
    get<PharmacyVisitDetail>(`/pharmacy/visits/${visitId}`),
  confirmPayment: (visitId: number) =>
    post<PharmacyVisitDetail>(`/pharmacy/visits/${visitId}/payment`),
  confirmPickup: (visitId: number) =>
    post<PharmacyVisitDetail>(`/pharmacy/visits/${visitId}/pickup`),
  completeInjection: (visitId: number) =>
    post<PharmacyVisitDetail>(`/pharmacy/visits/${visitId}/complete-injection`),

  getAdminUsers: () => get<AdminUser[]>('/admin/users'),
  getAdminRoles: () => get<{ id: number; roleCode: string; roleName: string }[]>('/admin/users/roles'),
  getPharmacyList: () => get<string[]>('/admin/users/pharmacy'),
  getReceptionList: () => get<string[]>('/admin/users/reception'),
  createUser: (payload: { username: string; password: string; realName: string; phone: string; idCard: string; roles?: string[]; department?: string; pharmacy?: string; receptionDesk?: string }) =>
    post<AdminUser>('/admin/users', payload),
  deleteUser: (userId: number) => remove<void>(`/admin/users/${userId}`),
  updateUser: (userId: number, payload: { realName: string; phone: string; idCard: string; roles?: string[]; department?: string; pharmacy?: string; receptionDesk?: string }) =>
    put<AdminUser>(`/admin/users/${userId}`, payload),
  uploadUserAvatar: (userId: number, file: File) => {
    const form = new FormData()
    form.append('file', file)
    return post<string>('/admin/users/' + userId + '/avatar', form, { timeout: 30000 })
  },
  uploadPatientAvatar: (patientId: number, file: File) => {
    const form = new FormData()
    form.append('file', file)
    return post<string>('/patients/' + patientId + '/avatar', form, { timeout: 30000 })
  },
  uploadMyAvatar: (file: File) => {
    const form = new FormData()
    form.append('file', file)
    return post<string>('/auth/profile/avatar', form, { timeout: 30000 })
  },
  updateProfile: (payload: { phone: string; idCard: string }) =>
    put<void>('/auth/profile', payload),
  changePassword: (payload: { oldPassword: string; newPassword: string }) =>
    post<void>('/auth/change-password', payload),
  search: (q: string) => get<SearchResult>('/search', { params: { q } }),
  searchPatients: (q: string) => get<Patient[]>('/search/patients', { params: { q } }),
  syncSearch: () => post<{ engine: string; message: string }>('/search/sync'),
  getTimeline: (patientId: number) => get<TimelineResult>(`/timeline/patients/${patientId}`),

  // ===== 前台工作台 =====
  getReceptionDashboard: () => get<{ todayTotal: number; todayPending: number; todayCompleted: number; todayPatients: number; queue: VisitDetail[]; departmentStats: Record<string, { total: number; pending: number; completed: number }>; recentRegistrations: VisitDetail[] }>('/dashboard/reception'),

  // ===== 患者端接口 =====
  patientRegister: (payload: { username: string; password: string; name: string; gender: string; age: number; phone?: string; idCard?: string; address?: string; allergyHistory?: string; medicalHistory?: string }) =>
    post<{ message: string; userId: number }>('/patient-center/register', payload),
  getPatientProfile: () => get<Record<string, unknown>>('/patient-center/profile'),
  updatePatientProfile: (payload: { phone?: string; address?: string; allergyHistory?: string; medicalHistory?: string }) =>
    put<void>('/patient-center/profile', payload),
  getPatientTimeline: () => get<TimelineResult>('/patient-center/timeline'),
  patientSelfRegister: (payload: { department: string; doctorName: string; appointmentDate: string; timeSlot: string }) =>
    post<Record<string, unknown>>('/patient-center/registration', payload),
  getPatientRegistrations: () => get<VisitDetail[]>('/patient-center/registrations'),
  cancelPatientRegistration: (visitId: number) => post<void>(`/patient-center/registrations/${visitId}/cancel`),
  getPatientDoctors: (department?: string) =>
    get<{ id: number; realName: string; username: string; department: string }[]>('/patient-center/doctors', {
      params: department ? { department } : undefined,
    }),
  sendAiMessage: (message: string) => post<{ reply: string }>('/patient-center/ai/chat', { message }),
  sendDoctorAiMessage: (message: string) => post<{ reply: string }>('/ai/chat', { message }),
  getHealthNews: () => get<HealthNewsItem[]>('/patient-center/health-news'),
  uploadMyPatientAvatar: (file: File) => {
    const form = new FormData()
    form.append('file', file)
    return post<string>('/patient-center/avatar', form, { timeout: 30000 })
  },
}
