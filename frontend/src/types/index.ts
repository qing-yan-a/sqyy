export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface UserProfile {
  id: number
  username: string
  realName: string
  phone?: string
  idCard?: string
  avatar?: string
  roles: string[]
  department?: string
  pharmacy?: string
  receptionDesk?: string
  patientId?: number | null
}

export interface LoginResult {
  token: string
  user: UserProfile
}

export interface Patient {
  id: number
  patientNo: string
  name: string
  gender: string
  age: number
  phone?: string
  idCard?: string
  address?: string
  allergyHistory?: string
  medicalHistory?: string
  avatar?: string
  createdAt: string
}

export interface DiagnosisRecord {
  id: number
  visitId: number
  diagnosisName: string
  diagnosisType: string
  description: string
}

export interface PrescriptionRecord {
  id: number
  visitId: number
  drugId: number
  drugName: string
  dosage?: string
  frequency?: string
  days: number
  quantity: number
  unitPrice: number
  lineAmount: number
  drugType: string
  paymentStatus: string
  pickupStatus: string
  injectionStatus: string
  paidAt?: string
  pickedUpAt?: string
  injectionCompletedAt?: string
}

export interface VisitRecord {
  id: number
  patientId: number
  visitNo: string
  doctorName: string
  department: string
  chiefComplaint: string
  visitTime: string
  notes?: string
  status?: string
  queueNumber?: number
}

export interface VisitDetail {
  visit: VisitRecord
  patient: Patient
  diagnoses: DiagnosisRecord[]
  prescriptions: PrescriptionRecord[]
}

export interface MedicationSummary {
  drugName: string
  dosage?: string
  frequency?: string
  days: number
  quantity: number
  visitTime: string
}

export interface Drug {
  id: number
  drugCode: string
  drugName: string
  specification: string
  manufacturer?: string
  unit: string
  stock: number
  reservedStock: number
  availableStock: number
  warningStock: number
  unitPrice: number
  drugType?: string
  createdAt: string
}

export interface PharmacyVisitDetail {
  visit: VisitRecord
  patient: Patient
  diagnoses: DiagnosisRecord[]
  prescriptions: PrescriptionRecord[]
  totalAmount: number
  paymentStatus: string
  pickupStatus: string
  injectionStatus: string
  hasInjection: boolean
}

export interface InventoryLog {
  id: number
  drugId: number
  drugName: string
  changeType: string
  quantity: number
  beforeStock: number
  afterStock: number
  operatorName: string
  remark?: string
  createdAt: string
}

export interface AdminUser {
  id: number
  username: string
  realName: string
  idCard?: string
  avatar?: string
  department?: string
  pharmacy?: string
  receptionDesk?: string
  roles: string[]
}

export interface SearchResult {
  keyword: string
  patients: Patient[]
  visits: VisitDetail[]
  drugs: Drug[]
  engine: string
}

export interface TimelineEvent {
  type: string
  time: string
  title: string
  description: string
  visitId: number
}

export interface TimelineResult {
  patient: Patient
  events: TimelineEvent[]
  medications: MedicationSummary[]
}

export interface HealthNewsItem {
  title: string
  summary: string
  image: string
  url: string
  source: string
}
