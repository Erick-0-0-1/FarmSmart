import React, { useState, useEffect } from 'react';
import { useLanguage } from '../context/LanguageContext';
import WeatherCard from '../components/WeatherCard';
import SoilAnalysisModal from '../components/SoilAnalysisModal';
import { getForecast } from '../services/weatherServices';
import { analyzeSoilMoisture } from '../services/soilAnalysisService';

function WeatherCalendar() {
  const { t } = useLanguage();
  const [forecast, setForecast] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSoilModalOpen, setIsSoilModalOpen] = useState(false);

  useEffect(() => {
    loadForecast();
  }, []);

  const loadForecast = async () => {
    try {
      setLoading(true);
      const data = await getForecast();
      setForecast(data);
    } catch (error) {
      console.error('Failed to load forecast:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSoilAnalysis = async (imageFile, forecastData) => {
    // Pass the forecast to the analysis service
    const result = await analyzeSoilMoisture(imageFile, forecastData || forecast);
    return result;
  };

  const getOverallWeatherSummary = () => {
    if (forecast.length === 0) return null;

    const perfectDays = forecast.filter(d => d.weatherAnalysis === 'PERFECT').length;
    const goodDays = forecast.filter(d => d.weatherAnalysis === 'GOOD').length;
    const rainyDays = forecast.filter(d => d.totalRainfall > 5).length;
    const totalRainfall = forecast.reduce((sum, d) => sum + d.totalRainfall, 0);

    let summary = '';
    let icon = '';
    let color = '';

    if (perfectDays >= 4) {
      summary = 'Excellent week for farming activities!';
      icon = '☀️';
      color = 'bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300';
    } else if (goodDays >= 4) {
      summary = 'Good weather conditions expected';
      icon = '🌤️';
      color = 'bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300';
    } else if (rainyDays >= 4) {
      summary = 'Rainy week ahead - plan indoor activities';
      icon = '🌧️';
      color = 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300';
    } else {
      summary = 'Mixed weather conditions this week';
      icon = '⛅';
      color = 'bg-yellow-100 dark:bg-yellow-900 text-yellow-700 dark:text-yellow-300';
    }

    return {
      summary,
      icon,
      color,
      perfectDays,
      goodDays,
      rainyDays,
      totalRainfall: totalRainfall.toFixed(1),
    };
  };

  const weatherSummary = getOverallWeatherSummary();

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <div className="text-center">
          <div className="text-6xl mb-4">🌤️</div>
          <p className="text-xl text-gray-600 dark:text-gray-400">Loading weather forecast...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-2">
          🌤️ Weather Forecast
        </h1>
        <p className="text-gray-600 dark:text-gray-400">
          7-day weather forecast and smart farming recommendations
        </p>
      </div>

      {/* Overall Summary Card */}
      {weatherSummary && (
        <div className={`${weatherSummary.color} rounded-lg p-6 mb-8 shadow-md`}>
          <div className="flex items-center justify-between flex-wrap gap-4">
            <div className="flex items-center gap-4">
              <div className="text-5xl">{weatherSummary.icon}</div>
              <div>
                <h2 className="text-2xl font-bold mb-1">{weatherSummary.summary}</h2>
                <p className="text-sm opacity-90">
                  {weatherSummary.perfectDays} perfect days • {weatherSummary.goodDays} good days • {weatherSummary.rainyDays} rainy days
                </p>
              </div>
            </div>
            <div className="text-right">
              <p className="text-sm opacity-75">Total Rainfall</p>
              <p className="text-3xl font-bold">{weatherSummary.totalRainfall} mm</p>
            </div>
          </div>
        </div>
      )}

      {/* AI Soil Analysis Button */}
      <div className="mb-8">
        <button
          onClick={() => setIsSoilModalOpen(true)}
          className="w-full sm:w-auto px-6 py-4 bg-gradient-to-r from-green-600 to-green-700 dark:from-green-700 dark:to-green-800 text-white rounded-lg hover:from-green-700 hover:to-green-800 dark:hover:from-green-600 dark:hover:to-green-700 transition font-medium shadow-lg hover:shadow-xl flex items-center justify-center gap-3"
        >
          <span className="text-2xl">🌱</span>
          <div className="text-left">
            <div className="font-bold">AI Soil Moisture Analysis</div>
            <div className="text-sm opacity-90">Upload photo for smart irrigation advice</div>
          </div>
        </button>
      </div>

      {/* Weather Cards Grid */}
      <div>
        <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
          📅 7-Day Detailed Forecast
        </h2>

        {forecast.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-6xl mb-4">🌤️</div>
            <p className="text-gray-500 dark:text-gray-400 text-lg">
              No weather data available
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {forecast.map((day, index) => (
              <WeatherCard
                key={index}
                day={index}
                weather={day}
              />
            ))}
          </div>
        )}
      </div>

      {/* Info Section */}
      <div className="mt-12 bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800 rounded-lg p-6">
        <h3 className="text-lg font-semibold text-blue-900 dark:text-blue-300 mb-3">
          ℹ️ How to Use This Forecast
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-blue-800 dark:text-blue-400">
          <div>
            <h4 className="font-semibold mb-2">Weather Ratings:</h4>
            <ul className="space-y-1">
              <li>• <span className="font-bold text-green-600 dark:text-green-400">Perfect</span> - Ideal for all farming activities</li>
              <li>• <span className="font-bold text-blue-600 dark:text-blue-400">Good</span> - Suitable for most tasks</li>
              <li>• <span className="font-bold text-yellow-600 dark:text-yellow-400">Acceptable</span> - Proceed with caution</li>
              <li>• <span className="font-bold text-orange-600 dark:text-orange-400">Poor</span> - Avoid field work if possible</li>
              <li>• <span className="font-bold text-red-600 dark:text-red-400">Bad</span> - Stay indoors, unsafe conditions</li>
            </ul>
          </div>
          <div>
            <h4 className="font-semibold mb-2">Best For:</h4>
            <ul className="space-y-1">
              <li>• <strong>Planting:</strong> Perfect or Good days with low rainfall</li>
              <li>• <strong>Fertilizing:</strong> Perfect days, no rain for 2-3 days after</li>
              <li>• <strong>Spraying:</strong> Perfect days with low wind and humidity</li>
              <li>• <strong>Harvesting:</strong> Good days with dry conditions</li>
            </ul>
          </div>
        </div>
      </div>

      {/* Soil Analysis Modal */}
      <SoilAnalysisModal
        isOpen={isSoilModalOpen}
        onClose={() => setIsSoilModalOpen(false)}
        onAnalyze={handleSoilAnalysis}
        forecast={forecast}
      />
    </div>
  );
}

export default WeatherCalendar;