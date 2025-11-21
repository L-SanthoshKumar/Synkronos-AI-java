import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const authService = {
  setToken: (token) => {
    if (token) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    } else {
      delete api.defaults.headers.common['Authorization']
    }
  },

  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password })
    return response.data
  },

  register: async (userData) => {
    const response = await api.post('/auth/register', userData)
    return response.data
  },

  getCurrentUser: async () => {
    const response = await api.get('/users/me')
    return response.data
  },
}

export const jobService = {
  getAllJobs: async () => {
    const response = await api.get('/jobs')
    return response.data
  },

  getJobById: async (id) => {
    const response = await api.get(`/jobs/${id}`)
    return response.data
  },

  searchJobs: async (query) => {
    const response = await api.get('/jobs/search', { params: { q: query } })
    return response.data
  },

  createJob: async (jobData) => {
    const response = await api.post('/jobs', jobData)
    return response.data
  },

  updateJob: async (id, jobData) => {
    const response = await api.put(`/jobs/${id}`, jobData)
    return response.data
  },

  deleteJob: async (id) => {
    const response = await api.delete(`/jobs/${id}`)
    return response.data
  },

  getMyJobs: async () => {
    const response = await api.get('/jobs/recruiter/my-jobs')
    return response.data
  },
}

export const applicationService = {
  applyToJob: async (jobId, coverLetter) => {
    const response = await api.post('/applications', { jobId, coverLetter })
    return response.data
  },

  getMyApplications: async () => {
    const response = await api.get('/applications/my-applications')
    return response.data
  },

  getApplicationsByJob: async (jobId) => {
    const response = await api.get(`/applications/job/${jobId}`)
    return response.data
  },

  updateApplicationStatus: async (applicationId, status) => {
    const response = await api.put(`/applications/${applicationId}/status`, { status })
    return response.data
  },
}

export const userService = {
  getUserById: async (id) => {
    const response = await api.get(`/users/${id}`)
    return response.data
  },

  updateUser: async (id, userData) => {
    const response = await api.put(`/users/${id}`, userData)
    return response.data
  },
}

export const fileService = {
  uploadResume: async (file) => {
    const formData = new FormData()
    formData.append('file', file)
    const response = await api.post('/upload/resume', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  },
}

export default api

