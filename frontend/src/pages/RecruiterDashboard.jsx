import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { jobService, applicationService } from '../services/api'
import { useAuth } from '../contexts/AuthContext'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, LineChart, Line } from 'recharts'
import toast from 'react-hot-toast'

const RecruiterDashboard = () => {
  const { user } = useAuth()
  const [stats, setStats] = useState({
    totalJobs: 0,
    activeJobs: 0,
    totalApplications: 0,
    pendingApplications: 0,
  })
  const [jobs, setJobs] = useState([])
  const [applicationsByJob, setApplicationsByJob] = useState([])

  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    try {
      const myJobs = await jobService.getMyJobs()
      setJobs(myJobs)
      
      const activeJobs = myJobs.filter(j => j.status === 'ACTIVE')
      setStats({
        totalJobs: myJobs.length,
        activeJobs: activeJobs.length,
        totalApplications: 0,
        pendingApplications: 0,
      })

      // Fetch applications for each job
      const allApplications = []
      for (const job of activeJobs) {
        try {
          const applications = await applicationService.getApplicationsByJob(job.id)
          allApplications.push(...applications)
        } catch (error) {
          console.error(`Failed to fetch applications for job ${job.id}`)
        }
      }

      setApplicationsByJob(allApplications)
      setStats(prev => ({
        ...prev,
        totalApplications: allApplications.length,
        pendingApplications: allApplications.filter(a => a.status === 'PENDING').length,
      }))
    } catch (error) {
      toast.error('Failed to load dashboard data')
    }
  }

  const chartData = jobs.map(job => ({
    name: job.title.length > 15 ? job.title.substring(0, 15) + '...' : job.title,
    applications: applicationsByJob.filter(a => a.jobId === job.id).length,
  }))

  const statusData = [
    { name: 'Pending', value: applicationsByJob.filter(a => a.status === 'PENDING').length },
    { name: 'Reviewing', value: applicationsByJob.filter(a => a.status === 'REVIEWING').length },
    { name: 'Shortlisted', value: applicationsByJob.filter(a => a.status === 'SHORTLISTED').length },
    { name: 'Rejected', value: applicationsByJob.filter(a => a.status === 'REJECTED').length },
  ]

  return (
    <div>
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Recruiter Dashboard
          </h1>
          <Link
            to="/jobs"
            className="bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700"
          >
            Post New Job
          </Link>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-sm font-medium text-gray-500">Total Jobs</h3>
            <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalJobs}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-sm font-medium text-gray-500">Active Jobs</h3>
            <p className="text-3xl font-bold text-green-600 mt-2">{stats.activeJobs}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-sm font-medium text-gray-500">Total Applications</h3>
            <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalApplications}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-sm font-medium text-gray-500">Pending Review</h3>
            <p className="text-3xl font-bold text-yellow-600 mt-2">{stats.pendingApplications}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
          {/* Applications by Job Chart */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-4">Applications by Job</h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" angle={-45} textAnchor="end" height={100} />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="applications" fill="#0ea5e9" />
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Application Status Chart */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-4">Application Status</h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={statusData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="value" fill="#10b981" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Recent Jobs */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4">My Job Postings</h2>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Title
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Applications
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {jobs.map((job) => {
                  const jobApplications = applicationsByJob.filter(a => a.jobId === job.id)
                  return (
                    <tr key={job.id}>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">{job.title}</div>
                        <div className="text-sm text-gray-500">{job.location}</div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                          job.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                        }`}>
                          {job.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {jobApplications.length}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <Link
                          to={`/jobs/${job.id}`}
                          className="text-primary-600 hover:text-primary-900 mr-4"
                        >
                          View
                        </Link>
                        <Link
                          to={`/applications?jobId=${job.id}`}
                          className="text-primary-600 hover:text-primary-900"
                        >
                          Applications
                        </Link>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RecruiterDashboard

