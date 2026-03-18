import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import FieldCard from '../components/FieldCard';
import FieldModal from '../components/FieldModal';
import WeatherCalendar from './WeatherCalendar';
import PlantingPage from './PlantingPage';
import { useLanguage } from '../context/LanguageContext';
import { getCurrentWeather } from '../services/weatherServices';
import { getFields, createField, updateField, deleteField } from '../services/fieldServices';
import FertilizerSchedulePage from './FertilizerSchedulePage';

function Dashboard() {
  const { t } = useLanguage();
  const [currentPage, setCurrentPage] = useState('dashboard');
  const [weather, setWeather] = useState(null);
  const [fields, setFields] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedField, setSelectedField] = useState(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [weatherData, fieldsData] = await Promise.all([
        getCurrentWeather(),
        getFields()
      ]);
      setWeather(weatherData);
      setFields(fieldsData);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateField = async (fieldData) => {
    try {
      if (selectedField) {
        // Update existing field
        await updateField(selectedField.id, fieldData);
      } else {
        // Create new field
        await createField(fieldData);
      }
      await loadData(); // Reload fields
      setIsModalOpen(false);
      setSelectedField(null);
    } catch (error) {
      console.error('Failed to save field:', error);
      alert('Failed to save field. Please try again.');
    }
  };

  const handleView = (field) => {
    // TODO: Navigate to field details page
    console.log('View field:', field);
    alert(`Viewing field: ${field.name}\n(Details page coming soon!)`);
  };

  const handleEdit = (field) => {
    setSelectedField(field);
    setIsModalOpen(true);
  };

  const getWeatherEmoji = (condition) => {
    const lower = condition?.toLowerCase() || '';
    if (lower.includes('clear') || lower.includes('sunny')) return '☀️';
    if (lower.includes('cloud')) return '☁️';
    if (lower.includes('rain')) return '🌧️';
    if (lower.includes('storm')) return '⛈️';
    return '🌤️';
  };

  // Render different pages based on currentPage
  if (currentPage === 'weather') {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
        <Navbar currentPage={currentPage} setCurrentPage={setCurrentPage} />
        <WeatherCalendar />
      </div>
    );
  }


  if (currentPage === 'planting') {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
        <Navbar currentPage={currentPage} setCurrentPage={setCurrentPage} />
        <PlantingPage />
      </div>
    );
  }

  if (currentPage === 'fertilizer') {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
        <Navbar currentPage={currentPage} setCurrentPage={setCurrentPage} />
        <FertilizerSchedulePage />
      </div>
    );
  }

  // Placeholder pages for other navigation items
  if (currentPage === 'fields') {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
        <Navbar currentPage={currentPage} setCurrentPage={setCurrentPage} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-20">
            <div className="text-6xl mb-4">🌾</div>
            <h2 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
              My Fields Page
            </h2>
            <p className="text-gray-600 dark:text-gray-400">
              Coming soon! This will show all your fields in detail.
            </p>
            <button
              onClick={() => setCurrentPage('dashboard')}
              className="mt-6 px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition"
            >
              Back to Dashboard
            </button>
          </div>
        </main>
      </div>
    );
  }

  if (currentPage === 'planting') {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
        <Navbar currentPage={currentPage} setCurrentPage={setCurrentPage} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-20">
            <div className="text-6xl mb-4">🌱</div>
            <h2 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
              Planting Records
            </h2>
            <p className="text-gray-600 dark:text-gray-400">
              Coming soon! Track your plantings and crop cycles.
            </p>
            <button
              onClick={() => setCurrentPage('dashboard')}
              className="mt-6 px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition"
            >
              Back to Dashboard
            </button>
          </div>
        </main>
      </div>
    );
  }

  if (currentPage === 'marketplace') {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
        <Navbar currentPage={currentPage} setCurrentPage={setCurrentPage} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-20">
            <div className="text-6xl mb-4">🛒</div>
            <h2 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
              Marketplace
            </h2>
            <p className="text-gray-600 dark:text-gray-400">
              Coming soon! Buy and sell agricultural products.
            </p>
            <button
              onClick={() => setCurrentPage('dashboard')}
              className="mt-6 px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition"
            >
              Back to Dashboard
            </button>
          </div>
        </main>
      </div>
    );
  }

  if (currentPage === 'community') {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
        <Navbar currentPage={currentPage} setCurrentPage={setCurrentPage} />
        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center py-20">
            <div className="text-6xl mb-4">👥</div>
            <h2 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
              Community Forum
            </h2>
            <p className="text-gray-600 dark:text-gray-400">
              Coming soon! Connect with other farmers, share tips and experiences.
            </p>
            <button
              onClick={() => setCurrentPage('dashboard')}
              className="mt-6 px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition"
            >
              Back to Dashboard
            </button>
          </div>
        </main>
      </div>
    );
  }

  // Default: Dashboard page
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-200">
      {/* Navigation */}
      <Navbar currentPage={currentPage} setCurrentPage={setCurrentPage} />

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Section */}
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
            {t('welcome')}, Farmer! 👋
          </h2>
          <p className="text-gray-600 dark:text-gray-400">
            {new Date().toLocaleDateString('en-US', {
              weekday: 'long',
              year: 'numeric',
              month: 'long',
              day: 'numeric',
            })}
          </p>
        </div>

        {loading ? (
          <div className="text-center py-20">
            <div className="text-6xl mb-4">🌾</div>
            <p className="text-xl text-gray-600 dark:text-gray-400">Loading your dashboard...</p>
          </div>
        ) : (
          <>
            {/* Stats Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
              {/* Active Fields */}
              <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 hover:shadow-lg transition">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-gray-500 dark:text-gray-400 text-sm font-medium">
                      {t('activeFields')}
                    </p>
                    <p className="text-4xl font-bold text-green-600 dark:text-green-400 mt-2">
                      {fields.length}
                    </p>
                  </div>
                  <div className="text-5xl">🌾</div>
                </div>
              </div>

              {/* Weather */}
              {weather && (
                <div
                  onClick={() => setCurrentPage('weather')}
                  className="bg-gradient-to-br from-blue-500 to-blue-600 dark:from-blue-600 dark:to-blue-800 rounded-lg shadow-md p-6 text-white hover:shadow-lg transition cursor-pointer"
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-blue-100 text-sm font-medium">{t('weatherToday')}</p>
                      <p className="text-4xl font-bold mt-2">{Math.round(weather.temperature)}°C</p>
                      <p className="text-blue-100 text-sm mt-1">{weather.condition}</p>
                    </div>
                    <div className="text-5xl">{getWeatherEmoji(weather.condition)}</div>
                  </div>
                  <div className="mt-3 pt-3 border-t border-blue-400">
                    <p className="text-xs text-blue-100">Click to view 5-day forecast →</p>
                  </div>
                </div>
              )}

              {/* Upcoming Tasks */}
              <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 hover:shadow-lg transition">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-gray-500 dark:text-gray-400 text-sm font-medium">
                      {t('upcomingTasks')}
                    </p>
                    <p className="text-4xl font-bold text-orange-600 dark:text-orange-400 mt-2">3</p>
                  </div>
                  <div className="text-5xl">📅</div>
                </div>
              </div>
            </div>

            {/* Fields Section */}
            <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
                <h3 className="text-2xl font-bold text-gray-900 dark:text-white">{t('myFields')}</h3>
                <button
                  onClick={() => {
                    setSelectedField(null);
                    setIsModalOpen(true);
                  }}
                  className="w-full sm:w-auto px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition font-medium shadow-md hover:shadow-lg"
                >
                  + {t('createField')}
                </button>
              </div>

              {fields.length === 0 ? (
                <div className="text-center py-16">
                  <div className="text-6xl mb-4">🌾</div>
                  <p className="text-gray-500 dark:text-gray-400 text-lg mb-2 font-medium">
                    No fields yet
                  </p>
                  <p className="text-gray-400 dark:text-gray-500 mb-6">
                    Create your first field to start tracking your crops!
                  </p>
                  <button
                    onClick={() => setIsModalOpen(true)}
                    className="px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition font-medium"
                  >
                    Create Your First Field
                  </button>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {fields.map((field) => (
                    <FieldCard
                      key={field.id}
                      field={field}
                      onView={handleView}
                      onEdit={handleEdit}
                    />
                  ))}
                </div>
              )}
            </div>
          </>
        )}
      </main>

      {/* Field Modal */}
      <FieldModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedField(null);
        }}
        onSubmit={handleCreateField}
        field={selectedField}
      />
    </div>
  );
}

export default Dashboard;