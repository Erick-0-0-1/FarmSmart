import React, { useState } from 'react';

function SoilAnalysisModal({ isOpen, onClose, onAnalyze, forecast }) {
  const [selectedImage, setSelectedImage] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysis, setAnalysis] = useState(null);

  const handleImageSelect = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedImage(file);

      // Create preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleAnalyze = async () => {
    if (!selectedImage) return;

    setIsAnalyzing(true);

    try {
      // Call the analysis service
      const result = await onAnalyze(selectedImage, forecast);
      setAnalysis(result);
    } catch (error) {
      console.error('Analysis failed:', error);
      alert('Failed to analyze soil. Please try again.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleClose = () => {
    setSelectedImage(null);
    setImagePreview(null);
    setAnalysis(null);
    onClose();
  };

  const handleNewAnalysis = () => {
    setSelectedImage(null);
    setImagePreview(null);
    setAnalysis(null);
  };

  const getUrgencyColor = (urgency) => {
    const colors = {
      CRITICAL: 'bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-300 border-red-500',
      HIGH: 'bg-orange-100 dark:bg-orange-900 text-orange-700 dark:text-orange-300 border-orange-500',
      MEDIUM: 'bg-yellow-100 dark:bg-yellow-900 text-yellow-700 dark:text-yellow-300 border-yellow-500',
      LOW: 'bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300 border-green-500',
    };
    return colors[urgency] || 'bg-gray-100 dark:bg-gray-900 text-gray-700 dark:text-gray-300 border-gray-500';
  };

  const getUrgencyIcon = (urgency) => {
    const icons = {
      CRITICAL: '🚨',
      HIGH: '⚠️',
      MEDIUM: '💧',
      LOW: '✅',
    };
    return icons[urgency] || '💧';
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700 flex justify-between items-center sticky top-0 bg-white dark:bg-gray-800 z-10">
          <div>
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
              🌱 AI Soil Moisture Analysis
            </h2>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
              Upload a photo of your field to get smart irrigation recommendations
            </p>
          </div>
          <button
            onClick={handleClose}
            className="text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 text-2xl"
          >
            ×
          </button>
        </div>

        {/* Content */}
        <div className="px-6 py-4">
          {!analysis ? (
            <>
              {/* Image Upload Area */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  📸 Upload Field Photo
                </label>

                {!imagePreview ? (
                  <label className="flex flex-col items-center justify-center w-full h-64 border-2 border-gray-300 dark:border-gray-600 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 transition">
                    <div className="flex flex-col items-center justify-center pt-5 pb-6">
                      <svg className="w-12 h-12 mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                      </svg>
                      <p className="mb-2 text-sm text-gray-500 dark:text-gray-400">
                        <span className="font-semibold">Click to upload</span> or drag and drop
                      </p>
                      <p className="text-xs text-gray-500 dark:text-gray-400">
                        PNG, JPG or JPEG (MAX. 10MB)
                      </p>
                    </div>
                    <input
                      type="file"
                      className="hidden"
                      accept="image/*"
                      onChange={handleImageSelect}
                    />
                  </label>
                ) : (
                  <div className="relative">
                    <img
                      src={imagePreview}
                      alt="Field preview"
                      className="w-full h-64 object-cover rounded-lg"
                    />
                    <button
                      onClick={handleNewAnalysis}
                      className="absolute top-2 right-2 px-3 py-1 bg-red-500 text-white rounded-md hover:bg-red-600 transition text-sm"
                    >
                      Change Photo
                    </button>
                  </div>
                )}
              </div>

              {/* Tips */}
              <div className="bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800 rounded-lg p-4 mb-6">
                <h3 className="text-sm font-semibold text-blue-900 dark:text-blue-300 mb-2">
                  📝 Tips for Best Results:
                </h3>
                <ul className="text-sm text-blue-800 dark:text-blue-400 space-y-1">
                  <li>• Take photo in good lighting (morning or late afternoon)</li>
                  <li>• Show the soil surface clearly</li>
                  <li>• Include some rice plants if available</li>
                  <li>• Avoid shadows or blurry images</li>
                </ul>
              </div>

              {/* Analyze Button */}
              <button
                onClick={handleAnalyze}
                disabled={!selectedImage || isAnalyzing}
                className="w-full px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition font-medium disabled:bg-gray-400 disabled:cursor-not-allowed"
              >
                {isAnalyzing ? (
                  <span className="flex items-center justify-center">
                    <svg className="animate-spin h-5 w-5 mr-3" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                    </svg>
                    Analyzing Soil Condition...
                  </span>
                ) : (
                  '🤖 Analyze Soil with AI'
                )}
              </button>
            </>
          ) : (
            <>
              {/* Analysis Results */}
              <div className="space-y-6">
                {/* Uploaded Image */}
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">
                    📸 Analyzed Image
                  </h3>
                  <img
                    src={imagePreview}
                    alt="Analyzed field"
                    className="w-full h-48 object-cover rounded-lg"
                  />
                </div>

                {/* Smart Water Recommendation */}
                <div className={`border-2 rounded-lg p-6 ${getUrgencyColor(analysis.urgency)}`}>
                  <div className="flex items-start gap-4">
                    <div className="text-5xl">
                      {getUrgencyIcon(analysis.urgency)}
                    </div>
                    <div className="flex-1">
                      <h3 className="text-xl font-bold mb-2">
                        {analysis.recommendation}
                      </h3>
                      <p className="text-sm opacity-90">
                        {analysis.detailedAdvice}
                      </p>
                    </div>
                  </div>
                </div>

                {/* Soil Analysis Details */}
                <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-6">
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                    🔍 Soil Analysis Details
                  </h3>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400">Moisture Level</p>
                      <p className="text-lg font-bold text-gray-900 dark:text-white">
                        {analysis.moistureLevel}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400">Soil Condition</p>
                      <p className="text-lg font-bold text-gray-900 dark:text-white">
                        {analysis.soilCondition}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400">Crack Severity</p>
                      <p className="text-lg font-bold text-gray-900 dark:text-white">
                        {analysis.crackSeverity}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600 dark:text-gray-400">Plant Stress</p>
                      <p className="text-lg font-bold text-gray-900 dark:text-white">
                        {analysis.plantStress}
                      </p>
                    </div>
                  </div>
                </div>

                {/* Weather-Based Timing */}
                <div className="bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800 rounded-lg p-6">
                  <h3 className="text-lg font-semibold text-blue-900 dark:text-blue-300 mb-3">
                    🌤️ Weather-Based Timing
                  </h3>
                  <p className="text-sm text-blue-800 dark:text-blue-400 mb-3">
                    {analysis.weatherContext}
                  </p>
                  <div className="text-sm text-blue-800 dark:text-blue-400">
                    <strong>Best Action:</strong> {analysis.actionTiming}
                  </div>
                </div>

                {/* Action Buttons */}
                <div className="flex gap-3">
                  <button
                    onClick={handleNewAnalysis}
                    className="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 transition font-medium"
                  >
                    📸 Analyze Another Photo
                  </button>
                  <button
                    onClick={handleClose}
                    className="flex-1 px-4 py-2 bg-green-600 dark:bg-green-700 text-white rounded-md hover:bg-green-700 dark:hover:bg-green-600 transition font-medium"
                  >
                    Done
                  </button>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default SoilAnalysisModal;