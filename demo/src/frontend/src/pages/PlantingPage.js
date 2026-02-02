import React, { useState, useEffect } from 'react';
import { useLanguage } from '../context/LanguageContext';
import PlantingCard from '../components/PlantingCard';
import PlantingModal from '../components/PlantingModal';
import { getPlantings, createPlanting, updatePlanting, markAsHarvested } from '../services/plantingService';
import { getFields } from '../services/fieldServices';
import { getVarieties } from '../services/varietyService';

function PlantingPage() {
  const { t } = useLanguage();
  const [plantings, setPlantings] = useState([]);
  const [fields, setFields] = useState([]);
  const [varieties, setVarieties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedPlanting, setSelectedPlanting] = useState(null);
  const [filter, setFilter] = useState('ALL'); // ALL, PLANNING, PLANTED, GROWING, HARVESTED

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [plantingsData, fieldsData, varietiesData] = await Promise.all([
        getPlantings(),
        getFields(),
        getVarieties()
      ]);
      setPlantings(plantingsData);
      setFields(fieldsData);
      setVarieties(varietiesData);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePlanting = async (plantingData) => {
    try {
      if (selectedPlanting) {
        await updatePlanting(selectedPlanting.id, plantingData);
      } else {
        await createPlanting(plantingData);
      }
      await loadData();
      setIsModalOpen(false);
      setSelectedPlanting(null);
    } catch (error) {
      console.error('Failed to save planting:', error);
      alert('Failed to save planting. Please try again.');
    }
  };

  const handleView = (planting) => {
    // TODO: Navigate to planting details page
    console.log('View planting:', planting);
    alert(`Viewing ${planting.riceVariety?.name} planting\n(Details page coming soon!)`);
  };

  const handleEdit = (planting) => {
    setSelectedPlanting(planting);
    setIsModalOpen(true);
  };

  const handleHarvest = async (planting) => {
    const confirmed = window.confirm(
      `Mark ${planting.riceVariety?.name} as harvested today?`
    );
    if (confirmed) {
      try {
        await markAsHarvested(planting.id);
        await loadData();
      } catch (error) {
        console.error('Failed to mark as harvested:', error);
        alert('Failed to mark as harvested. Please try again.');
      }
    }
  };

  const getFilteredPlantings = () => {
    if (filter === 'ALL') return plantings;
    return plantings.filter(p => p.status === filter);
  };

  const filteredPlantings = getFilteredPlantings();

  const getStatusCounts = () => {
    return {
      all: plantings.length,
      planning: plantings.filter(p => p.status === 'PLANNING').length,
      planted: plantings.filter(p => p.status === 'PLANTED').length,
      growing: plantings.filter(p => p.status === 'GROWING').length,
      harvested: plantings.filter(p => p.status === 'HARVESTED').length,
    };
  };

  const statusCounts = getStatusCounts();

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-20">
          <div className="text-6xl mb-4">🌱</div>
          <p className="text-xl text-gray-600 dark:text-gray-400">Loading plantings...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-2">
          🌱 Rice Plantings
        </h1>
        <p className="text-gray-600 dark:text-gray-400">
          Track your rice crops from planting to harvest
        </p>
      </div>

      {/* Status Filter Tabs */}
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
          onClick={() => setFilter('PLANNING')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'PLANNING'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          📋 Planning ({statusCounts.planning})
        </button>
        <button
          onClick={() => setFilter('PLANTED')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'PLANTED'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          🌱 Planted ({statusCounts.planted})
        </button>
        <button
          onClick={() => setFilter('GROWING')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'GROWING'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          🌾 Growing ({statusCounts.growing})
        </button>
        <button
          onClick={() => setFilter('HARVESTED')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filter === 'HARVESTED'
              ? 'bg-green-600 dark:bg-green-700 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          ✅ Harvested ({statusCounts.harvested})
        </button>
      </div>

      {/* Create Button */}
      <div className="mb-6">
        <button
          onClick={() => {
            setSelectedPlanting(null);
            setIsModalOpen(true);
          }}
          className="w-full sm:w-auto px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition font-medium shadow-md hover:shadow-lg"
        >
          + New Planting
        </button>
      </div>

      {/* No Fields Warning */}
      {fields.length === 0 && (
        <div className="bg-yellow-50 dark:bg-yellow-900/30 border border-yellow-200 dark:border-yellow-800 rounded-lg p-6 mb-6">
          <div className="flex items-start gap-3">
            <span className="text-3xl">⚠️</span>
            <div>
              <h3 className="text-lg font-semibold text-yellow-900 dark:text-yellow-300 mb-2">
                No Fields Created Yet
              </h3>
              <p className="text-sm text-yellow-800 dark:text-yellow-400 mb-3">
                You need to create at least one field before you can start planting.
              </p>
              <button
                onClick={() => window.location.href = '#/dashboard'}
                className="px-4 py-2 bg-yellow-600 text-white rounded-md hover:bg-yellow-700 transition text-sm font-medium"
              >
                Go to Dashboard to Create Field
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Plantings List */}
      {filteredPlantings.length === 0 ? (
        <div className="text-center py-16 bg-white dark:bg-gray-800 rounded-lg shadow-md">
          <div className="text-6xl mb-4">🌾</div>
          <p className="text-gray-500 dark:text-gray-400 text-lg mb-2 font-medium">
            {filter === 'ALL' ? 'No plantings yet' : `No ${filter.toLowerCase()} plantings`}
          </p>
          <p className="text-gray-400 dark:text-gray-500 mb-6">
            {fields.length === 0
              ? 'Create a field first, then start tracking your rice crops!'
              : 'Click "New Planting" to start tracking your rice crops!'
            }
          </p>
          {fields.length > 0 && (
            <button
              onClick={() => setIsModalOpen(true)}
              className="px-6 py-3 bg-green-600 dark:bg-green-700 text-white rounded-lg hover:bg-green-700 dark:hover:bg-green-600 transition font-medium"
            >
              Create Your First Planting
            </button>
          )}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredPlantings.map((planting) => (
            <PlantingCard
              key={planting.id}
              planting={planting}
              onView={handleView}
              onEdit={handleEdit}
              onHarvest={handleHarvest}
            />
          ))}
        </div>
      )}

      {/* Planting Modal */}
      <PlantingModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedPlanting(null);
        }}
        onSubmit={handleCreatePlanting}
        planting={selectedPlanting}
        fields={fields}
        varieties={varieties}
      />
    </div>
  );
}

export default PlantingPage;