import React from 'react';

function FertilizerRecommendationModal({ isOpen, onClose, application, recommendation }) {
  if (!isOpen || !recommendation) return null;

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
    return icons[urgency] || '📋';
  };

  const getWeatherIcon = (condition) => {
    if (!condition) return '🌤️';
    const lower = condition.toLowerCase();
    if (lower.includes('clear') || lower.includes('sunny')) return '☀️';
    if (lower.includes('cloud')) return '☁️';
    if (lower.includes('rain') || lower.includes('shower')) return '🌧️';
    if (lower.includes('storm')) return '⛈️';
    return '🌤️';
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    const today = new Date();

    if (date.toDateString() === today.toDateString()) return 'Today';
    if (date.toDateString() === new Date(today.getTime() + 86400000).toDateString()) return 'Tomorrow';

    return date.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700 flex justify-between items-center sticky top-0 bg-white dark:bg-gray-800 z-10">
          <div>
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
              💚 Smart Fertilizer Recommendation
            </h2>
            <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
              AI-powered analysis combining weather forecast and scheduling
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 text-2xl"
          >
            ×
          </button>
        </div>

        {/* Content */}
        <div className="px-6 py-4 space-y-6">
          {/* Main Recommendation */}
          <div className={`border-2 rounded-lg p-6 ${getUrgencyColor(recommendation.urgency)}`}>
            <div className="flex items-start gap-4">
              <div className="text-5xl">
                {getUrgencyIcon(recommendation.urgency)}
              </div>
              <div className="flex-1">
                <h3 className="text-xl font-bold mb-2">
                  {recommendation.recommendation}
                </h3>
                <p className="text-sm opacity-90">
                  {recommendation.detailedAdvice}
                </p>
              </div>
            </div>
          </div>

          {/* Application Details */}
          <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              📋 Application Details
            </h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">Fertilizer Type</p>
                <p className="text-lg font-bold text-gray-900 dark:text-white">
                  {application.fertilizerType}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">Amount</p>
                <p className="text-lg font-bold text-gray-900 dark:text-white">
                  {application.amount} kg/ha
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">Scheduled Date</p>
                <p className="text-lg font-bold text-gray-900 dark:text-white">
                  {formatDate(application.scheduledDate)}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">Days from Planting</p>
                <p className="text-lg font-bold text-gray-900 dark:text-white">
                  Day {recommendation.daysFromPlanting || application.daysAfterPlanting || 'N/A'}
                </p>
              </div>
            </div>
          </div>

          {/* Weather Analysis */}
          <div className="bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800 rounded-lg p-6">
            <h3 className="text-lg font-semibold text-blue-900 dark:text-blue-300 mb-4">
              🌤️ 7-Day Weather Analysis
            </h3>

            {/* Weather Summary */}
            <div className="mb-4">
              <p className="text-sm text-blue-800 dark:text-blue-400">
                <strong>Analysis Period:</strong> {recommendation.analysisStartDate ? formatDate(recommendation.analysisStartDate) : 'Next 7 days'}
              </p>
              <p className="text-sm text-blue-800 dark:text-blue-400">
                <strong>Rainy Days:</strong> {recommendation.rainyDaysCount || 0} days
              </p>
              <p className="text-sm text-blue-800 dark:text-blue-400">
                <strong>Total Rainfall:</strong> {recommendation.totalRainfall?.toFixed(1) || '0.0'} mm
              </p>
            </div>

            {/* Daily Forecast */}
            {recommendation.weatherForecast && recommendation.weatherForecast.length > 0 && (
              <div className="space-y-2">
                <p className="text-sm font-semibold text-blue-900 dark:text-blue-300 mb-2">
                  Daily Forecast:
                </p>
                {recommendation.weatherForecast.map((day, index) => (
                  <div key={index} className="flex items-center justify-between bg-white dark:bg-gray-800 rounded p-3">
                    <div className="flex items-center gap-3">
                      <span className="text-2xl">{getWeatherIcon(day.condition)}</span>
                      <div>
                        <p className="font-medium text-gray-900 dark:text-white">
                          {formatDate(day.date)}
                        </p>
                        <p className="text-xs text-gray-600 dark:text-gray-400">
                          {day.condition}
                        </p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="font-bold text-gray-900 dark:text-white">
                        {Math.round(day.temperature || day.avgTemp || 0)}°C
                      </p>
                      <p className="text-xs text-gray-600 dark:text-gray-400">
                        🌧️ {(day.rainfall || day.totalRainfall || 0).toFixed(1)} mm
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Weather Context */}
          <div className="bg-purple-50 dark:bg-purple-900/30 border border-purple-200 dark:border-purple-800 rounded-lg p-6">
            <h3 className="text-lg font-semibold text-purple-900 dark:text-purple-300 mb-3">
              🔍 Weather Context
            </h3>
            <p className="text-sm text-purple-800 dark:text-purple-400">
              {recommendation.weatherContext || 'Analyzing weather patterns for optimal application timing...'}
            </p>
          </div>

          {/* Best Action */}
          <div className="bg-green-50 dark:bg-green-900/30 border border-green-200 dark:border-green-800 rounded-lg p-6">
            <h3 className="text-lg font-semibold text-green-900 dark:text-green-300 mb-3">
              ✅ Recommended Action
            </h3>
            <p className="text-sm text-green-800 dark:text-green-400">
              {recommendation.actionTiming || recommendation.recommendation}
            </p>
          </div>

          {/* Alternative Options */}
          {recommendation.alternativeDates && recommendation.alternativeDates.length > 0 && (
            <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-6">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
                📅 Alternative Application Dates
              </h3>
              <div className="space-y-2">
                {recommendation.alternativeDates.map((alt, index) => (
                  <div key={index} className="flex items-center justify-between p-3 bg-white dark:bg-gray-800 rounded">
                    <div>
                      <p className="font-medium text-gray-900 dark:text-white">
                        {formatDate(alt.date)}
                      </p>
                      <p className="text-xs text-gray-600 dark:text-gray-400">
                        {alt.reason}
                      </p>
                    </div>
                    <span className="text-sm text-green-600 dark:text-green-400 font-medium">
                      {alt.score || 'Good'} option
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Close Button */}
          <div className="flex justify-end pt-4 border-t border-gray-200 dark:border-gray-700">
            <button
              onClick={onClose}
              className="px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition font-medium"
            >
              Got It!
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default FertilizerRecommendationModal;