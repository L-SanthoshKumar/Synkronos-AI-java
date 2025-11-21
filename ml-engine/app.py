"""
Synkronos AI ML Engine - Resume Parsing and Candidate Scoring Service

This version is intentionally lightweight and avoids heavy native dependencies
so it can run on standard Python installations (like your current Python 3.13
on Windows) without needing C compilers or large ML libraries.
"""
from flask import Flask, request, jsonify
from flask_cors import CORS
import re
import os
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)
CORS(app)

# Common technical skills database (simple keyword list)
TECH_SKILLS = [
    'java', 'python', 'javascript', 'typescript', 'react', 'angular', 'vue',
    'node.js', 'spring boot', 'django', 'flask', 'express', 'mongodb',
    'postgresql', 'mysql', 'redis', 'aws', 'azure', 'docker', 'kubernetes',
    'git', 'ci/cd', 'jenkins', 'terraform', 'ansible', 'linux', 'unix',
    'microservices', 'rest api', 'graphql', 'sql', 'nosql', 'html', 'css',
    'sass', 'less', 'webpack', 'babel', 'npm', 'yarn', 'maven', 'gradle',
    'agile', 'scrum', 'devops', 'machine learning', 'ai', 'tensorflow',
    'pytorch', 'scikit-learn', 'pandas', 'numpy', 'data science', 'analytics'
]


def normalize_text(text: str) -> str:
    """Very simple normalization: lowercase and collapse whitespace."""
    if not text:
        return ""
    return re.sub(r"\s+", " ", text).strip().lower()


def extract_skills_from_text(text):
    """
    Extract technical skills from resume text using pure keyword matching.

    This avoids spaCy or any ML library so it works without native builds.
    """
    if not text:
        return set()

    text_norm = normalize_text(text)
    extracted_skills = set()

    for skill in TECH_SKILLS:
        if skill.lower() in text_norm:
            extracted_skills.add(skill.title())

    return extracted_skills


def extract_experience_years(text):
    """
    Extract years of experience from resume text
    """
    if not text:
        return 0
    
    # Pattern matching for years of experience
    patterns = [
        r'(\d+)\+?\s*years?\s*(?:of\s*)?experience',
        r'experience[:\s]+(\d+)\+?\s*years?',
        r'(\d+)\+?\s*years?\s*in',
    ]
    
    text_lower = text.lower()
    for pattern in patterns:
        match = re.search(pattern, text_lower)
        if match:
            try:
                return int(match.group(1))
            except:
                continue
    
    return 0


def calculate_skill_match_score(resume_skills, job_required_skills):
    """
    Calculate skill match percentage between resume and job requirements
    """
    if not job_required_skills or len(job_required_skills) == 0:
        return 100.0
    
    if not resume_skills or len(resume_skills) == 0:
        return 0.0
    
    # Normalize skills to lowercase for comparison
    resume_skills_lower = {s.lower() for s in resume_skills}
    job_skills_lower = {s.lower() for s in job_required_skills}
    
    # Find matching skills
    matching_skills = resume_skills_lower.intersection(job_skills_lower)
    
    # Calculate percentage
    match_percentage = (len(matching_skills) / len(job_skills_lower)) * 100
    
    return min(match_percentage, 100.0)


def calculate_experience_score(resume_years, job_min_years):
    """
    Calculate experience match score
    """
    if not job_min_years:
        return 100.0
    
    if resume_years >= job_min_years:
        return 100.0
    elif resume_years >= job_min_years * 0.7:
        return 70.0
    elif resume_years >= job_min_years * 0.5:
        return 50.0
    else:
        return (resume_years / job_min_years) * 50.0


def calculate_text_similarity(resume_text, job_description):
    """
    Approximate text similarity using a simple token-overlap Jaccard score.

    This replaces TF-IDF/cosine similarity so we don't depend on scikit-learn
    or numpy, which require native build tools on Windows.
    """
    if not resume_text or not job_description:
        return 50.0

    resume_tokens = set(normalize_text(resume_text).split())
    job_tokens = set(normalize_text(job_description).split())

    if not resume_tokens or not job_tokens:
        return 50.0

    intersection = resume_tokens.intersection(job_tokens)
    union = resume_tokens.union(job_tokens)
    score = (len(intersection) / len(union)) * 100
    return round(score, 2)


def calculate_overall_score(breakdown):
    """
    Calculate overall match score from breakdown components
    """
    weights = {
        'skillMatch': 0.4,
        'experienceMatch': 0.3,
        'textSimilarity': 0.3
    }
    
    overall = (
        breakdown.get('skillMatch', 0) * weights['skillMatch'] +
        breakdown.get('experienceMatch', 0) * weights['experienceMatch'] +
        breakdown.get('textSimilarity', 0) * weights['textSimilarity']
    )
    
    return round(overall, 2)


@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'service': 'Synkronos AI ML Engine',
        'version': '1.0.0'
    }), 200


@app.route('/predict-score', methods=['POST'])
def predict_score():
    """
    Main endpoint for calculating candidate-job match score
    
    Request body:
    {
        "resumeText": "string",
        "jobRequiredSkills": ["skill1", "skill2"],
        "jobMinYearsOfExperience": 5,
        "jobDescription": "string"
    }
    """
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({'error': 'Request body is required'}), 400
        
        resume_text = data.get('resumeText', '')
        job_required_skills = data.get('jobRequiredSkills', [])
        job_min_years = data.get('jobMinYearsOfExperience', 0)
        job_description = data.get('jobDescription', '')
        
        # Extract information from resume
        extracted_skills = extract_skills_from_text(resume_text)
        resume_years = extract_experience_years(resume_text)
        
        # Calculate individual scores
        skill_match_score = calculate_skill_match_score(
            list(extracted_skills),
            job_required_skills
        )
        
        experience_score = calculate_experience_score(
            resume_years,
            job_min_years
        )
        
        text_similarity_score = calculate_text_similarity(
            resume_text,
            job_description
        )
        
        # Create breakdown
        breakdown = {
            'skillMatch': skill_match_score,
            'experienceMatch': experience_score,
            'textSimilarity': text_similarity_score
        }
        
        # Calculate overall score
        overall_score = calculate_overall_score(breakdown)
        
        # Create skill match scores dictionary
        skill_match_scores = {}
        if job_required_skills:
            for skill in job_required_skills:
                skill_lower = skill.lower()
                if any(skill_lower in s.lower() or s.lower() in skill_lower 
                       for s in extracted_skills):
                    skill_match_scores[skill] = 100.0
                else:
                    skill_match_scores[skill] = 0.0
        
        # Prepare response
        response = {
            'overallScore': overall_score,
            'skillMatchScores': skill_match_scores,
            'breakdown': breakdown,
            'extractedSkills': list(extracted_skills)
        }
        
        return jsonify(response), 200
        
    except Exception as e:
        return jsonify({
            'error': 'Internal server error',
            'message': str(e)
        }), 500


@app.route('/extract-skills', methods=['POST'])
def extract_skills():
    """
    Extract skills from resume text
    """
    try:
        data = request.get_json()
        resume_text = data.get('resumeText', '')
        
        extracted_skills = extract_skills_from_text(resume_text)
        
        return jsonify({
            'skills': list(extracted_skills)
        }), 200
        
    except Exception as e:
        return jsonify({
            'error': 'Internal server error',
            'message': str(e)
        }), 500


if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    debug = os.getenv('FLASK_DEBUG', 'False').lower() == 'true'
    
    print(f"Starting Synkronos AI ML Engine on port {port}")
    print(f"Debug mode: {debug}")
    
    app.run(host='0.0.0.0', port=port, debug=debug)

