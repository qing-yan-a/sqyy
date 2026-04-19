import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { api } from '../api'
import type { UserProfile } from '../types'

const TOKEN_KEY = 'sqyy-token'
const USER_KEY = 'sqyy-user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) ?? '')
  const user = ref<UserProfile | null>(readUser())

  const isLoggedIn = computed(() => Boolean(token.value))
  const roleSet = computed(() => new Set(user.value?.roles ?? []))

  async function login(form: { username: string; password: string }) {
    const result = await api.login(form)
    token.value = result.token
    user.value = result.user
    localStorage.setItem(TOKEN_KEY, result.token)
    localStorage.setItem(USER_KEY, JSON.stringify(result.user))
  }

  async function refreshProfile() {
    if (!token.value) return
    user.value = await api.getProfile()
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
  }

  function hasRole(roles?: string[]) {
    if (!roles || roles.length === 0) return true
    return roles.some((role) => roleSet.value.has(role))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return {
    token,
    user,
    isLoggedIn,
    roleSet,
    login,
    refreshProfile,
    hasRole,
    logout,
  }
})

function readUser(): UserProfile | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as UserProfile
  } catch {
    return null
  }
}
