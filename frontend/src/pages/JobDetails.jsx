import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { jobService, applicationService } from '../services/api'
import { useAuth } from '../contexts/AuthContext'
import toast from 'react-hot-toast'

const JobDetails = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [job, setJob] = useState(null)
  const [coverLetter, setCoverLetter] = useState('')
  const [loading, setLoading] = useState(true)
  const [applying, setApplying] = useState(false)
  const [hasApplied, setHasApplied] = useState(false)

  useEffect(() => {
    fetchJobDetails()
    checkApplication()
  }, [id])

  const fetchJobDetails = async () => {
    try {
      const data = await jobService.getJobById(id)
      setJob(data)
    } catch (error) {
      toast.error('Failed to load job details')
      navigate('/jobs')
    } finally {
      setLoading(false)
    }
  }

  const checkApplication = async () => {
    if (user?.role !== 'JOB_SEEKER') return
    try {
      const applications = await applicationService.getMyApplications()
      const applied = applications.some(a => a.jobId === id)
      setHasApplied(applied)
    } catch (error) {
      console.error('Failed to check application status')
    }
  }

  const handleApply = async () => {
    if (!coverLetter.trim()) {
      toast.error('Please provide a cover letter')
      return
    }
    setApplying(true)
    try {
      await applicationService.applyToJob(id, coverLetter)
      toast.success('Application submitted successfully!')
      setHasApplied(true)
      setCoverLetter('')
    } catch (error) {
      toast.error(error.response?.data?.error || 'Failed to apply')
    } finally {
      setApplying(false)
    }
  }

  if (loading) {
    return (
      <div>
        <Navbar />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          </div>
        </div>
      </div>
    )
  }

  if (!job) return null

  return (
    <div>
      <Navbar />
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Link to="/jobs" className="text-primary-600 hover:text-primary-800 mb-4 inline-block">
          ← Back to Jobs
        </Link>

        <div className="bg-white rounded-lg shadow-lg p-8 mb-6">
          <h1 className="text-3xl font-bold mb-4">{job.title}</h1>
          <div className="flex items-center gap-4 mb-6">
            <span className="text-xl text-gray-600">{job.companyName}</span>
            <span className="text-gray-400">•</span>
            <span className="text-gray-600">{job.location}</span>
            <span className="text-gray-400">•</span>
            <span className="text-gray-600">{job.employmentType}</span>
          </div>

          {job.minSalary && job.maxSalary && (
            <div className="mb-6">
              <span className="text-2xl font-semibold text-green-600">
                ${job.minSalary.toLocaleString()} - ${job.maxSalary.toLocaleString()} {job.currency}
              </span>
            </div>
          )}

          <div className="mb-6">
            <h2 className="text-xl font-semibold mb-2">Description</h2>
            <p className="text-gray-700 whitespace-pre-line">{job.description}</p>
          </div>

          <div className="mb-6">
            <h2 className="text-xl font-semibold mb-2">Required Skills</h2>
            <div className="flex flex-wrap gap-2">
              {job.requiredSkills?.map((skill, idx) => (
                <span
                  key={idx}
                  className="px-3 py-1 bg-primary-100 text-primary-800 rounded-full text-sm"
                >
                  {skill}
                </span>
              ))}
            </div>
          </div>

          <div className="mb-6">
            <h2 className="text-xl font-semibold mb-2">Requirements</h2>
            <ul className="list-disc list-inside text-gray-700 space-y-1">
              {job.minYearsOfExperience && (
                <li>Minimum {job.minYearsOfExperience} years of experience</li>
              )}
              {job.educationLevel && (
                <li>Education: {job.educationLevel}</li>
              )}
            </ul>
          </div>
        </div>

        {user?.role === 'JOB_SEEKER' && (
          <div className="bg-white rounded-lg shadow-lg p-8">
            {hasApplied ? (
              <div className="text-center py-8">
                <p className="text-green-600 font-semibold text-lg mb-2">
                  ✓ You have already applied to this job
                </p>
                <Link to="/applications" className="text-primary-600 hover:text-primary-800">
                  View your applications →
                </Link>
              </div>
            ) : (
              <>
                <h2 className="text-2xl font-semibold mb-4">Apply for this Position</h2>
                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Cover Letter
                  </label>
                  <textarea
                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
                    rows="6"
                    placeholder="Tell us why you're a great fit for this position..."
                    value={coverLetter}
                    onChange={(e) => setCoverLetter(e.target.value)}
                  />
                </div>
                <button
                  onClick={handleApply}
                  disabled={applying}
                  className="bg-primary-600 text-white px-6 py-2 rounded-md hover:bg-primary-700 disabled:opacity-50"
                >
                  {applying ? 'Applying...' : 'Submit Application'}
                </button>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default JobDetails

