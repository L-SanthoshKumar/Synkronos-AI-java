import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { jobService, applicationService } from '../services/api'
import { useAuth } from '../contexts/AuthContext'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'
import toast from 'react-hot-toast'

const JobSeekerDashboard = () => {
  const { user } = useAuth()
  const [stats, setStats] = useState({
    totalApplications: 0,
    pending: 0,
    shortlisted: 0,
    rejected: 0,
  })
  const [recentApplications, setRecentApplications] = useState([])
  const [recommendedJobs, setRecommendedJobs] = useState([])

  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    try {
      const [applications, jobs] = await Promise.all([
        applicationService.getMyApplications(),
        jobService.getAllJobs(),
      ])

      const applicationStats = {
        totalApplications: applications.length,
        pending: applications.filter(a => a.status === 'PENDING').length,
        shortlisted: applications.filter(a => a.status === 'SHORTLISTED').length,
        rejected: applications.filter(a => a.status === 'REJECTED').length,
      }
      setStats(applicationStats)
      setRecentApplications(applications.slice(0, 5))

      // Simple recommendation based on skills
      if (user?.skills && user.skills.length > 0) {
        const recommended = jobs
          .filter(job => 
            job.requiredSkills?.some(skill => 
              user.skills.some(userSkill => 
                userSkill.toLowerCase().includes(skill.toLowerCase()) || 
                skill.toLowerCase().includes(userSkill.toLowerCase())
              )
            )
          )
          .slice(0, 5)
        setRecommendedJobs(recommended)
      } else {
        setRecommendedJobs(jobs.slice(0, 5))
      }
    } catch (error) {
      toast.error('Failed to load dashboard data')
    }
  }

  const chartData = [
    { name: 'Pending', value: stats.pending },
    { name: 'Shortlisted', value: stats.shortlisted },
    { name: 'Rejected', value: stats.rejected },
  ]

  const COLORS = ['#0088FE', '#00C49F', '#FF8042']

  return (
    <div>
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">
          Welcome back, {user?.firstName}!
        </h1>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-sm font-medium text-gray-500">Total Applications</h3>
            <p className="text-3xl font-bold text-gray-900 mt-2">{stats.totalApplications}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-sm font-medium text-gray-500">Pending</h3>
            <p className="text-3xl font-bold text-yellow-600 mt-2">{stats.pending}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-sm font-medium text-gray-500">Shortlisted</h3>
            <p className="text-3xl font-bold text-green-600 mt-2">{stats.shortlisted}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-sm font-medium text-gray-500">Rejected</h3>
            <p className="text-3xl font-bold text-red-600 mt-2">{stats.rejected}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
          {/* Application Status Chart */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-4">Application Status</h2>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={chartData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {chartData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Recent Applications */}
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-semibold mb-4">Recent Applications</h2>
            <div className="space-y-4">
              {recentApplications.length > 0 ? (
                recentApplications.map((app) => (
                  <div key={app.id} className="border-b pb-3">
                    <Link to={`/jobs/${app.jobId}`} className="font-medium text-primary-600 hover:text-primary-800">
                      {app.job?.title || 'Job'}
                    </Link>
                    <div className="flex justify-between items-center mt-1">
                      <span className={`text-sm px-2 py-1 rounded ${
                        app.status === 'SHORTLISTED' ? 'bg-green-100 text-green-800' :
                        app.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                        'bg-yellow-100 text-yellow-800'
                      }`}>
                        {app.status}
                      </span>
                      {app.matchScore && (
                        <span className="text-sm text-gray-600">
                          Match: {app.matchScore.toFixed(1)}%
                        </span>
                      )}
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-gray-500">No applications yet</p>
              )}
            </div>
          </div>
        </div>

        {/* Recommended Jobs */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4">Recommended Jobs</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {recommendedJobs.map((job) => (
              <Link
                key={job.id}
                to={`/jobs/${job.id}`}
                className="border rounded-lg p-4 hover:shadow-lg transition-shadow"
              >
                <h3 className="font-semibold text-lg mb-2">{job.title}</h3>
                <p className="text-sm text-gray-600 mb-2">{job.companyName}</p>
                <p className="text-sm text-gray-500">{job.location}</p>
                {job.minSalary && job.maxSalary && (
                  <p className="text-sm font-medium text-green-600 mt-2">
                    ${job.minSalary.toLocaleString()} - ${job.maxSalary.toLocaleString()}
                  </p>
                )}
              </Link>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default JobSeekerDashboard

