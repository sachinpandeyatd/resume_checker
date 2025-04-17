import React, { useState } from 'react';
import ResumeUploader from './component/ResumeUploader';

function App() {
  const [resumeFile, setResumeFile] = useState(null);

  const handleFileSelected = (file) => {
    setResumeFile(file);
  }

  const handleAnalyze = () => {
    if(!resumeFile){
      alert("Please select a file to upload");
      return;
    }

    console.log("Starting analysis for:", resumeFile.name);

    const formData = new FormData();
    formData.append('resumeFile', resumeFile);

    alert(`Analysis would start for ${resumeFile.name}. Backend call not implemented yet.`);
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col items-center justify-center p-4">
      <div className="bg-white shadow-md rounded-lg p-8 max-w-xl w-full">
        <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
          AI Resume Checker
        </h1>
        <ResumeUploader onFileSelect={handleFileSelected} />

         {/* Conditionally render the analyze button */}
         {resumeFile && (
              <button
                 onClick={handleAnalyze}
                 className="mt-6 w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 transition duration-200 ease-in-out font-semibold disabled:opacity-50 disabled:cursor-not-allowed"
                 disabled={!resumeFile} // Button disabled until a file is selected
             >
                 Analyze Resume
             </button>
         )}
      </div>
    </div>
  );
}

export default App
