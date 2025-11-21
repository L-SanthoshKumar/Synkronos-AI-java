import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { applicationService } from '../services/api'
import { useAuth } from '../contexts/AuthContext'
import toast from 'react-hot-toast'

const Applications = () => {
  const { user } = useAuth()
  const [searchParams] = useSearchParams()
  const jobId = searchParams.get('jobId')
  const [applications, setApplications] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchApplications()
  }, [jobId])

  const fetchApplications = async () => {
    try {
      setLoading(true)
      let data
      if (user?.role === 'RECRUITER' && jobId) {
        data = await applicationService.getApplicationsByJob(jobId)
      } else {
        data = await applicationService.getMyApplications()
      }
      setApplications(data)
    } catch (error) {
      toast.error('Failed to load applications')
    } finally {
      setLoading(false)
    }
  }

  const handleStatusUpdate = async (applicationId, newStatus) => {
    if (user?.role !== 'RECRUITER') return

    try {
      await applicationService.updateApplicationStatus(applicationId, newStatus)
      toast.success('Application status updated')
      fetchApplications()
    } catch (error) {
      toast.error('Failed to update status')
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'SHORTLISTED':
        return 'bg-green-100 text-green-800'
      case 'REJECTED':
        return 'bg-red-100 text-red-800'
      case 'REVIEWING':
        return 'bg-blue-100 text-blue-800'
      default:
        return 'bg-yellow-100 text-yellow-800'
    }
  }

  return (
    <div>
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">
          {user?.role === 'RECRUITER' ? 'Job Applications' : 'My Applications'}
        </h1>

        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          </div>
        ) : applications.length > 0 ? (
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  {user?.role === 'RECRUITER' && (
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Candidate
                    </th>
                  )}
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Job
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Match Score
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Applied Date
                  </th>
                  {user?.role === 'RECRUITER' && (
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Actions
                    </th>
                  )}
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {applications.map((app) => (
                  <tr key={app.id}>
                    {user?.role === 'RECRUITER' && (
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-sm font-medium text-gray-900">
                          {app.jobSeeker?.firstName} {app.jobSeeker?.lastName}
                        </div>
                        <div className="text-sm text-gray-500">{app.jobSeeker?.email}</div>
                      </td>
                    )}
                    <td className="px-6 py-4">
                      <Link
                        to={`/jobs/${app.jobId}`}
                        className="text-sm font-medium text-primary-600 hover:text-primary-900"
                      >
                        {app.job?.title || 'Job'}
                      </Link>
                      <div className="text-sm text-gray-500">{app.job?.companyName}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusColor(app.status)}`}>
                        {app.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {app.matchScore ? `${app.matchScore.toFixed(1)}%` : 'N/A'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(app.appliedAt).toLocaleDateString()}
                    </td>
                    {user?.role === 'RECRUITER' && (
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <select
                          value={app.status}
                          onChange={(e) => handleStatusUpdate(app.id, e.target.value)}
                          className="border border-gray-300 rounded-md px-2 py-1 text-sm"
                        >
                          <option value="PENDING">Pending</option>
                          <option value="REVIEWING">Reviewing</option>
                          <option value="SHORTLISTED">Shortlisted</option>
                          <option value="REJECTED">Rejected</option>
                          <option value="INTERVIEW_SCHEDULED">Interview Scheduled</option>
                          <option value="ACCEPTED">Accepted</option>
                        </select>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-gray-500">No applications found</p>
          </div>
        )}
      </div>
    </div>
  )
}

export default Applications

