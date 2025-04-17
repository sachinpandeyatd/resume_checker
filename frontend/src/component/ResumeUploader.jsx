import React, { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { FiUploadCloud, FiFileText, FiX } from 'react-icons/fi';

function ResumeUploader({onFileSelect}){
    const [selectedFile, setSelectedFile] = useState(null);
    const [error, setError] = useState('');

    const onFileDrop = useCallback((acceptedFiles, rejectedFiles) => {
        setError('');
        setSelectedFile(null);

        if(rejectedFiles && rejectedFiles.length > 0){
            setError(`File type not accepted. Please upload PDF, DOCX, or TXT.`);
            return;
        }

        if(acceptedFiles && acceptedFiles.length > 0){
            const file = acceptedFiles[0];
            setSelectedFile(file);

            if(onFileSelect){
                onFileSelect(file);
            }

            console.log(file);
        }
    }, [onFileSelect]);

    const {getRootProps, getInputProps, isDragActive, open} = useDropzone({
        onFileDrop,
        accept: {
            'application/pdf': ['.pdf'],
            'application/vnd.openxmlformats-officedocument.wordprocessingml.document': ['.docx'],
            'text/plain': ['.txt'],
        },
        multiple: false,
    });

    const removeFile = (e) => {
        e.stopPropagation();
        setSelectedFile(null);
        setError('');

        if(onFileSelect) onFileSelect(null);
    };

    return (
        <div className="w-full max-w-lg mx-auto">
            <div
                {...getRootProps()}
                className={`border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-colors duration-200 ease-in-out
                    ${isDragActive ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-gray-400'}
                    ${error ? 'border-red-500 bg-red-50' : ''}`}
            >
                <input {...getInputProps()} />

                {selectedFile ? (
                    <div className="flex flex-col items-center justify-center text-gray-700">
                        <FiFileText className="w-12 h-12 mb-4 text-blue-500" />
                        <p className="font-semibold">{selectedFile.name}</p>
                        <p className="text-sm text-gray-500">
                        {(selectedFile.size / 1024).toFixed(2)} KB
                        </p>
                        <button
                        onClick={removeFile}
                        className="mt-4 text-red-500 hover:text-red-700 text-sm font-semibold flex items-center"
                        aria-label="Remove file"
                        >
                        <FiX className="mr-1" /> Remove
                        </button>
                    </div>
                    ) : (
                    <div className="flex flex-col items-center justify-center text-gray-500">
                        <FiUploadCloud className="w-12 h-12 mb-4" />
                        {isDragActive ? (
                        <p className="font-semibold">Drop the file here ...</p>
                        ) : (
                        <>
                            <p className="font-semibold">Drag 'n' drop your resume here, or click to select</p>
                            <p className="text-sm mt-1">PDF, DOCX, or TXT (Max 5MB)</p>
                        </>
                        )}
                        <button type="button" onClick={open} className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 text-sm font-medium">
                            Select File
                        </button>
                    </div>
                    )}
            </div>
            {error && (
                <p className="mt-2 text-sm text-red-600">{error}</p>
            )}
        </div>
    )
}

export default ResumeUploader;