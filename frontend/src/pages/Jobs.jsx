import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { jobService } from '../services/api'
import { useAuth } from '../contexts/AuthContext'
import toast from 'react-hot-toast'

const Jobs = () => {
  const { user } = useAuth()
  const [jobs, setJobs] = useState([])
  const [searchTerm, setSearchTerm] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchJobs()
  }, [])

  const fetchJobs = async () => {
    try {
      setLoading(true)
      const data = user?.role === 'RECRUITER' 
        ? await jobService.getMyJobs()
        : await jobService.getAllJobs()
      setJobs(data)
    } catch (error) {
      toast.error('Failed to load jobs')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchJobs()
      return
    }
    try {
      setLoading(true)
      const data = await jobService.searchJobs(searchTerm)
      setJobs(data)
    } catch (error) {
      toast.error('Search failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            {user?.role === 'RECRUITER' ? 'My Job Postings' : 'Browse Jobs'}
          </h1>
          {user?.role === 'RECRUITER' && (
            <Link
              to="/jobs/new"
              className="bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700"
            >
              Post New Job
            </Link>
          )}
        </div>

        {/* Search Bar */}
        {user?.role === 'JOB_SEEKER' && (
          <div className="mb-8">
            <div className="flex gap-4">
              <input
                type="text"
                placeholder="Search jobs by title, company, or skills..."
                className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              />
              <button
                onClick={handleSearch}
                className="bg-primary-600 text-white px-6 py-2 rounded-md hover:bg-primary-700"
              >
                Search
              </button>
            </div>
          </div>
        )}

        {/* Jobs List */}
        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          </div>
        ) : jobs.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {jobs.map((job) => (
              <Link
                key={job.id}
                to={`/jobs/${job.id}`}
                className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow"
              >
                <h3 className="text-xl font-semibold mb-2">{job.title}</h3>
                <p className="text-gray-600 mb-2">{job.companyName}</p>
                <p className="text-sm text-gray-500 mb-4">{job.location}</p>
                {job.minSalary && job.maxSalary && (
                  <p className="text-sm font-medium text-green-600 mb-4">
                    ${job.minSalary.toLocaleString()} - ${job.maxSalary.toLocaleString()} {job.currency}
                  </p>
                )}
                <div className="flex flex-wrap gap-2 mb-4">
                  {job.requiredSkills?.slice(0, 3).map((skill, idx) => (
                    <span
                      key={idx}
                      className="px-2 py-1 bg-primary-100 text-primary-800 text-xs rounded"
                    >
                      {skill}
                    </span>
                  ))}
                  {job.requiredSkills?.length > 3 && (
                    <span className="px-2 py-1 bg-gray-100 text-gray-800 text-xs rounded">
                      +{job.requiredSkills.length - 3} more
                    </span>
                  )}
                </div>
                <span className={`text-xs px-2 py-1 rounded ${
                  job.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                }`}>
                  {job.status}
                </span>
              </Link>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-gray-500">No jobs found</p>
          </div>
        )}
      </div>
    </div>
  )
}

export default Jobs

