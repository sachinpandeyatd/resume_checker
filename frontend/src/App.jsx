import React, { useState } from 'react';
import ResumeUploader from './component/ResumeUploader';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

function App() {
  const [resumeFile, setResumeFile] = useState(null);
  const [analysisResult, setAnalysisResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(''); 

  const handleFileSelected = (file) => {
    setResumeFile(file);
    setAnalysisResult(null);
    setError('');
  }

  const handleAnalyze = () => {
    if(!resumeFile){
      error("Please select a resume file first")
      return;
    }

    setError('');
    setIsLoading(true);
    setAnalysisResult(null);

    const formData = new FormData();
    formData.append('resumeFile', resumeFile);

    fetch('http://localhost:8080/api/v1/check-resume', {
      method : 'POST',
      body : formData,
    }).then(response => {
      if(!response.ok){
        return response.text().then(text => {
          throw new Error(`Network response was not ok: ${response.status} ${response.statusText}. Server says: ${text || 'No additional error message'}`);
        })
      }
      return response.json();
    }).then(data => {
      if(data && data.message){
        setAnalysisResult(data.message);
      }else {
        setError("Received an unexpected response format from the server.");
      }
    }).catch(err => { // Changed variable name from 'error' to 'err' to avoid conflict with state
      console.error('Error uploading file:', err);
      setError(err.message || "An unknown error occurred during analysis.");
    })
    .finally(() => {
      setIsLoading(false);
    });
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center py-10 px-4">
      <div className="bg-white shadow-xl rounded-lg p-6 md:p-8 w-full max-w-2xl">
        <h1 className="text-3xl font-bold text-center text-gray-800 mb-8">
          AI Resume Analyzer
        </h1>
        <ResumeUploader onFileSelect={handleFileSelected} />

        {resumeFile && !isLoading && (
          <button
            onClick={handleAnalyze}
            className="mt-6 w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 transition duration-200 ease-in-out font-semibold"
          >
            Analyze Resume
          </button>
        )}

        {isLoading && (
          <div className="mt-6 text-center">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
            <p className="text-lg text-gray-600 mt-4">Analyzing your resume, please wait...</p>
          </div>
        )}

        {error && !isLoading && (
          <div className="mt-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
            <p className="font-bold">Error:</p>
            <p>{error}</p>
          </div>
        )}

        {analysisResult && !isLoading && !error && (
          <div className="mt-8 p-4 sm:p-6 border border-gray-200 rounded-lg bg-white shadow-md">
            <h2 className="text-xl sm:text-2xl font-semibold text-gray-800 mb-4 border-b border-gray-300 pb-3">
              Analysis Report
            </h2>
            {/* Apply more specific prose styles here */}
            <div className="prose prose-sm sm:prose-base max-w-none
                            prose-headings:font-semibold prose-headings:text-gray-700
                            prose-h3:text-lg prose-h3:sm:text-xl prose-h3:mt-5 prose-h3:mb-2 prose-h3:border-b prose-h3:border-gray-200 prose-h3:pb-2
                            prose-p:text-gray-600 prose-p:leading-relaxed
                            prose-ul:list-disc prose-ul:pl-5 prose-ul:sm:pl-6 prose-ul:space-y-1
                            prose-li:text-gray-600
                            prose-strong:text-gray-700">
              <ReactMarkdown remarkPlugins={[remarkGfm]}>
                {analysisResult}
              </ReactMarkdown>
            </div>
          </div>
        )}
      </div>
      <footer className="text-center text-gray-500 text-sm mt-8">
        Powered by Gemini AI
      </footer>
    </div>
  );
}

export default App