import React from 'react';

function WeatherCard({ day, weather }) {
  // Safety check
  if (!weather) return null;

  const getWeatherColor = (analysis) => {
    const colors = {
      PERFECT: 'bg-green-500 dark:bg-green-600',
      GOOD: 'bg-blue-500 dark:bg-blue-600',
      ACCEPTABLE: 'bg-yellow-500 dark:bg-yellow-600',
      POOR: 'bg-orange-500 dark:bg-orange-600',
      BAD: 'bg-red-500 dark:bg-red-600',
    };
    return colors[analysis] || 'bg-gray-500';
  };

  const getWeatherBorderColor = (analysis) => {
    const colors = {
      PERFECT: 'border-green-500 dark:border-green-400',
      GOOD: 'border-blue-500 dark:border-blue-400',
      ACCEPTABLE: 'border-yellow-500 dark:border-yellow-400',
      POOR: 'border-orange-500 dark:border-orange-400',
      BAD: 'border-red-500 dark:border-red-400',
    };
    return colors[analysis] || 'border-gray-500';
  };

  const getWeatherEmoji = (condition) => {
    if (!condition) return '🌤️';
    const lower = condition.toLowerCase();
    if (lower.includes('clear') || lower.includes('sunny')) return '☀️';
    if (lower.includes('cloud')) return '☁️';
    if (lower.includes('rain') || lower.includes('shower')) return '🌧️';
    if (lower.includes('storm') || lower.includes('thunder')) return '⛈️';
    if (lower.includes('snow')) return '❄️';
    if (lower.includes('mist') || lower.includes('fog')) return '🌫️';
    return '🌤️';
  };

  const getAnalysisText = (analysis) => {
    const texts = {
      PERFECT: 'Perfect!',
      GOOD: 'Good',
      ACCEPTABLE: 'Acceptable',
      POOR: 'Poor',
      BAD: 'Bad',
    };
    return texts[analysis] || 'Unknown';
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    const today = new Date();
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Today';
    }
    if (date.toDateString() === tomorrow.toDateString()) {
      return 'Tomorrow';
    }
    return date.toLocaleDateString('en-US', { weekday: 'short' });
  };

  // Handle both old and new field names from API
  const temperature = weather.temperature || weather.avgTemp || 0;
  const humidity = weather.humidity || weather.avgHumidity || 0;
  const rainfall = weather.rainfall || weather.totalRainfall || 0;

  return (
    <div className={`bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-all p-4 border-l-4 ${getWeatherBorderColor(weather.weatherAnalysis)}`}>
      {/* Date and Day */}
      <div className="text-center mb-3">
        <p className="text-lg font-bold text-gray-900 dark:text-white">
          {formatDate(weather.date)}
        </p>
        <p className="text-xs text-gray-500 dark:text-gray-400">
          {weather.date ? new Date(weather.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) : 'N/A'}
        </p>
      </div>

      {/* Weather Icon */}
      <div className="text-center mb-3">
        <div className="text-5xl mb-2">
          {getWeatherEmoji(weather.condition)}
        </div>
        <p className="text-sm text-gray-600 dark:text-gray-400">
          {weather.condition || 'N/A'}
        </p>
      </div>

      {/* Temperature */}
      <div className="text-center mb-3">
        <p className="text-3xl font-bold text-gray-900 dark:text-white">
          {Math.round(temperature)}°C
        </p>
        <p className="text-xs text-gray-500 dark:text-gray-400">
          {Math.round(weather.minTemp || temperature - 2)}° / {Math.round(weather.maxTemp || temperature + 2)}°
        </p>
      </div>

      {/* Weather Details */}
      <div className="space-y-2 mb-3">
        <div className="flex items-center justify-between text-sm">
          <span className="text-gray-600 dark:text-gray-400">💧 Humidity</span>
          <span className="font-medium text-gray-900 dark:text-white">{Math.round(humidity)}%</span>
        </div>
        <div className="flex items-center justify-between text-sm">
          <span className="text-gray-600 dark:text-gray-400">🌧️ Rainfall</span>
          <span className="font-medium text-gray-900 dark:text-white">{rainfall.toFixed(1)} mm</span>
        </div>
      </div>

      {/* Weather Analysis Badge */}
      <div className="text-center">
        <span className={`inline-block px-3 py-1 rounded-full text-white text-xs font-bold ${getWeatherColor(weather.weatherAnalysis)}`}>
          {getAnalysisText(weather.weatherAnalysis)}
        </span>
      </div>

      {/* Farming Recommendation */}
      <div className="mt-3 pt-3 border-t border-gray-200 dark:border-gray-700">
        <p className="text-xs text-gray-600 dark:text-gray-400 text-center">
          {weather.recommendation || 'No recommendation available'}
        </p>
      </div>
    </div>
  );
}

export default WeatherCard;