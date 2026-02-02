import React, { useState, useEffect } from 'react';
import { useLanguage } from '../context/LanguageContext';
import FertilizerCard from '../components/FertilizerCard';
import FertilizerRecommendationModal from '../components/FertilizerRecommendationModal';
import { getAllApplications, getRecommendation, markAsCompleted, postponeApplication, skipApplication, generateSchedule } from '../services/fertilizerService';
import { getPlantings } from '../services/plantingService';

function FertilizerSchedulePage() {
  const { t } = useLanguage();
  const [applications, setApplications] = useState([]);
  const [plantings, setPlantings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isRecommendationModalOpen, setIsRecommendationModalOpen] = useState(false);
  const [selectedApplication, setSelectedApplication] = useState(null);
  const [selectedRecommendation, setSelectedRecommendation] = useState(null);
  const [filter, setFilter] = useState('ALL'); // ALL, PENDING, CRITICAL, HIGH, COMPLETED

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [applicationsData, plantingsData] = await Promise.all([
        getAllApplications(),
        getPlantings()
      ]);
      setApplications(applicationsData);
      setPlantings(plantingsData);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleViewRecommendation = async (application) => {
    try {
      setSelectedApplication(application);
      const recommendation = await getRecommendation(application.id);
      setSelectedRecommendation(recommendation);
      setIsRecommendationModalOpen(true);
    } catch (error) {
      console.error('Failed to load recommendation:', error);
      alert('Failed to load recommendation. Please try again.');
    }
  };

  const handleMarkCompleted = async (application) => {
    const confirmed = window.confirm(
      `Mark ${application.fertilizerType} application as completed?`
    );
    if (confirmed) {
      try {
        await markAsCompleted(application.id);
        await loadData();
      } catch (error) {
        console.error('Failed to mark as completed:', error);
        alert('Failed to mark as completed. Please try again.');
      }
    }
  };

  const handlePostpone = async (application) => {
    const newDate = prompt('Enter new date (YYYY-MM-DD):');
    if (newDate) {
      const reason = prompt('Reason for postponement:', 'Weather conditions not suitable');
      try {
        await postponeApplication(application.id, {
          newScheduledDate: newDate,
          reason: reason || 'Postponed'
        });
        await loadData();
      } catch (error) {
        console.error('Failed to postpone:', error);
        alert('Failed to postpone application. Please try again.');
      }
    }
  };

  const handleSkip = async (application) => {
    const reason = prompt('Reason for skipping this application:', 'Not needed');
    if (reason !== null) {
      const confirmed = window.confirm(
        `Skip ${application.fertilizerType} application? This cannot be undone.`
      );
      if (confirmed) {
        try {
          await skipApplication(application.id, reason);
          await loadData();
        } catch (error) {
          console.error('Failed to skip:', error);
          alert('Failed to skip application. Please try again.');
        }
      }
    }
  };

  const handleGenerateSchedules = async () => {
    const growingPlantings = plantings.filter(p =>
      p.status === 'GROWING' || p.status === 'PLANTED'
    );

    if (growingPlantings.length === 0) {
      alert('No active plantings found. Create a planting first!');
      return;
    }

    const confirmed = window.confirm(
      `Generate fertilizer schedules for ${growingPlantings.length} active planting(s)?`
    );

    if (confirmed) {
      try {
        setLoading(true);
        for (const planting of growingPlantings) {
          try {
            await generateSchedule(planting.id);
          } catch (error) {
            console.error(`Failed to generate schedule for planting ${planting.id}:`, error);
          }
        }
        await loadData();
        alert(`✅ Fertilizer schedules generated successfully!`);
      } catch (error) {
        console.error('Failed to generate schedules:', error);
        alert('Failed to generate some schedules. Please try again.');
      } finally {
        setLoading(false);
      }
    }
  };

  const getFilteredApplications = () => {
    if (filter === 'ALL') return applications;
    if (filter === 'PENDING') return applications.filter(a => a.status === 'PENDING');
    if (filter === 'CRITICAL') return applications.filter(a => a.urgency === 'CRITICAL' && a.status === 'PENDING');
    if (filter === 'HIGH') return applications.filter(a => a.urgency === 'HIGH' && a.status === 'PENDING');
    if (filter === 'COMPLETED') return applications.filter(a => a.status === 'COMPLETED');
    return applications;
  };

  const filteredApplications = getFilteredApplications();

  const getStatusCounts = () => {
    return {
      all: applications.length,
      pending: applications.filter(a => a.status === 'PENDING').length,
      critical: applications.filter(a => a.urgency === 'CRITICAL' && a.status === 'PENDING').length,
      high: applications.filter(a => a.urgency === 'HIGH' && a.status === 'PENDING').length,
      completed: applications.filter(a => a.status === 'COMPLETED').length,
    };
  };

  const statusCounts = getStatusCounts();

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-20">
          <div className="text-6xl mb-4">💚</div>
          <p className="text-xl text-gray-600 dark:text-gray-400">Loading fertilizer schedules...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-2">
          💚 Smart Fertilizer Schedule
        </h1>
        <p className="text-gray-600 dark:text-gray-400">
          AI-powered fertilizer recommendations based on weather forecasts
        </p>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4">
          <p className="text-sm text-gray-600 dark:text-gray-400">Total Applications</p>
          <p className="text-3xl font-bold text-gray-900 dark:text-white">{statusCounts.all}</p>
        </div>
        <div className="bg-yellow-50 dark:bg-yellow-900/30 rounded-lg shadow-md p-4">
          <p className="text-sm text-yellow-700 dark:text-yellow-400">⏳ Pending</p>
          <p className="text-3xl font-bold text-yellow-700 dark:text-yellow-400">{statusCounts.pending}</p>
        </div>
        <div className="bg-red-50 dark:bg-red-900/30 rounded-lg shadow-md p-4">
          <p className="text-sm text-red-700 dark:text-red-400">🚨 Critical</p>
          <p className="text-3xl font-bold text-red-700 dark:text-red-400">{statusCounts.critical}</p>
        </div>
        <div className="bg-green-50 dark:bg-green-900/30 rounded-lg shadow-md p-4">
          <p className="text-sm text-green-700 dark:text-green-400">✅ Completed</p>
          <p className="text-3xl font-bold text-green-700 dark:text-green-400">{statusCounts.completed}</p>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="mb-6 flex flex-wrap gap-2">
        <button
          onClick={() => setFilter('ALL')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'ALL'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          All ({statusCounts.all})
        </button>
        <button
          onClick={() => setFilter('PENDING')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'PENDING'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          ⏳ Pending ({statusCounts.pending})
        </button>
        <button
          onClick={() => setFilter('CRITICAL')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'CRITICAL'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          🚨 Critical ({statusCounts.critical})
        </button>
        <button
          onClick={() => setFilter('HIGH')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'HIGH'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          ⚠️ High Priority ({statusCounts.high})
        </button>
        <button
          onClick={() => setFilter('COMPLETED')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'COMPLETED'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          ✅ Completed ({statusCounts.completed})
        </button>
      </div>

      {/* Generate Schedules Button */}
      <div className="mb-6">
        <button
          onClick={handleGenerateSchedules}
          className="w-full sm:w-auto px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition font-medium shadow-md hover:shadow-lg"
        >
          🌾 Generate Fertilizer Schedules
        </button>
        <p className="text-xs text-gray-600 dark:text-gray-400 mt-2">
          Automatically creates fertilizer schedules for all active plantings based on rice variety requirements
        </p>
      </div>

      {/* Applications List */}
      {filteredApplications.length === 0 ? (
        <div className="text-center py-16 bg-white dark:bg-gray-800 rounded-lg shadow-md">
          <div className="text-6xl mb-4">💚</div>
          <p className="text-gray-500 dark:text-gray-400 text-lg mb-2 font-medium">
            {filter === 'ALL' ? 'No fertilizer applications yet' : `No ${filter.toLowerCase()} applications`}
          </p>
          <p className="text-gray-400 dark:text-gray-500 mb-6">
            {applications.length === 0
              ? 'Generate fertilizer schedules for your active plantings to get started!'
              : `Switch to "All" to see all applications`
            }
          </p>
          {applications.length === 0 && plantings.length > 0 && (
            <button
              onClick={handleGenerateSchedules}
              className="px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition font-medium"
            >
              Generate Schedules Now
            </button>
          )}
          {plantings.length === 0 && (
            <p className="text-gray-500 dark:text-gray-400">
              Create a planting first from the Planting page!
            </p>
          )}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredApplications.map((application) => (
            <FertilizerCard
              key={application.id}
              application={application}
              onViewRecommendation={handleViewRecommendation}
              onMarkCompleted={handleMarkCompleted}
              onPostpone={handlePostpone}
              onSkip={handleSkip}
            />
          ))}
        </div>
      )}

      {/* Info Section */}
      <div className="mt-12 bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800 rounded-lg p-6">
        <h3 className="text-lg font-semibold text-blue-900 dark:text-blue-300 mb-3">
          ℹ️ How Smart Fertilizer Schedule Works
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-blue-800 dark:text-blue-400">
          <div>
            <h4 className="font-semibold mb-2">Urgency Levels:</h4>
            <ul className="space-y-1">
              <li>• <span className="font-bold text-red-600 dark:text-red-400">🚨 CRITICAL</span> - Apply immediately, even in bad weather</li>
              <li>• <span className="font-bold text-orange-600 dark:text-orange-400">⚠️ HIGH</span> - Apply within 1-2 days</li>
              <li>• <span className="font-bold text-yellow-600 dark:text-yellow-400">💧 MEDIUM</span> - Monitor weather, apply when suitable</li>
              <li>• <span className="font-bold text-green-600 dark:text-green-400">✅ LOW</span> - On schedule, wait for perfect weather</li>
            </ul>
          </div>
          <div>
            <h4 className="font-semibold mb-2">Smart Recommendations:</h4>
            <ul className="space-y-1">
              <li>• Analyzes 7-day weather forecast</li>
              <li>• Combines scheduling with weather data</li>
              <li>• Prevents fertilizer waste in rain</li>
              <li>• Alerts for critical applications</li>
              <li>• Suggests optimal application dates</li>
            </ul>
          </div>
        </div>
      </div>

      {/* Recommendation Modal */}
      <FertilizerRecommendationModal
        isOpen={isRecommendationModalOpen}
        onClose={() => {
          setIsRecommendationModalOpen(false);
          setSelectedApplication(null);
          setSelectedRecommendation(null);
        }}
        application={selectedApplication}
        recommendation={selectedRecommendation}
      />
    </div>
  );
}

export default FertilizerSchedulePage;