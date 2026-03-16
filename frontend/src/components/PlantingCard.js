import React from 'react';
import { useLanguage } from '../context/LanguageContext';

function PlantingCard({ planting, onView, onEdit, onHarvest }) {
  const { t } = useLanguage();

  const getStatusColor = (status) => {
    const colors = {
      PLANNING: 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300',
      PLANTED: 'bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300',
      GROWING: 'bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300',
      HARVESTED: 'bg-yellow-100 dark:bg-yellow-900 text-yellow-700 dark:text-yellow-300',
      CANCELLED: 'bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-300',
    };
    return colors[status] || 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300';
  };

  const getStatusIcon = (status) => {
    const icons = {
      PLANNING: '📋',
      PLANTED: '🌱',
      GROWING: '🌾',
      HARVESTED: '✅',
      CANCELLED: '❌',
    };
    return icons[status] || '📋';
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };

  const getDaysUntilHarvest = () => {
    if (!planting.expectedHarvestDate || planting.status === 'HARVESTED') return null;

    const today = new Date();
    const harvestDate = new Date(planting.expectedHarvestDate);
    const diffTime = harvestDate - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays < 0) return { text: 'Overdue!', color: 'text-red-600 dark:text-red-400' };
    if (diffDays === 0) return { text: 'Today!', color: 'text-green-600 dark:text-green-400' };
    if (diffDays <= 7) return { text: `${diffDays} days`, color: 'text-orange-600 dark:text-orange-400' };
    return { text: `${diffDays} days`, color: 'text-gray-600 dark:text-gray-400' };
  };

  const daysUntilHarvest = getDaysUntilHarvest();

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-shadow p-6 border-l-4 border-green-500">
      {/* Header */}
      <div className="flex justify-between items-start mb-4">
        <div className="flex-1">
          <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-1">
            {planting.riceVariety?.name || 'Unknown Variety'}
          </h3>
          <p className="text-sm text-gray-600 dark:text-gray-400">
            {planting.field?.name || 'Unknown Field'}
          </p>
        </div>
        <span className={`px-3 py-1 rounded-full text-xs font-bold ${getStatusColor(planting.status)}`}>
          {getStatusIcon(planting.status)} {planting.status}
        </span>
      </div>

      {/* Variety Info */}
      {planting.riceVariety && (
        <div className="mb-4 p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
          <div className="grid grid-cols-2 gap-2 text-sm">
            <div>
              <span className="text-gray-600 dark:text-gray-400">Maturity:</span>
              <span className="ml-2 font-medium text-gray-900 dark:text-white">
                {planting.riceVariety.maturityDays} days
              </span>
            </div>
            <div>
              <span className="text-gray-600 dark:text-gray-400">Season:</span>
              <span className="ml-2 font-medium text-gray-900 dark:text-white">
                {planting.riceVariety.season}
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Timeline */}
      <div className="space-y-3 mb-4">
        <div className="flex items-center text-sm">
          <span className="text-lg mr-2">🌱</span>
          <div className="flex-1">
            <p className="text-gray-600 dark:text-gray-400">Planted</p>
            <p className="font-medium text-gray-900 dark:text-white">
              {formatDate(planting.plantingDate)}
            </p>
          </div>
        </div>

        <div className="flex items-center text-sm">
          <span className="text-lg mr-2">🌾</span>
          <div className="flex-1">
            <p className="text-gray-600 dark:text-gray-400">Expected Harvest</p>
            <p className="font-medium text-gray-900 dark:text-white">
              {formatDate(planting.expectedHarvestDate)}
            </p>
          </div>
          {daysUntilHarvest && (
            <span className={`text-sm font-bold ${daysUntilHarvest.color}`}>
              {daysUntilHarvest.text}
            </span>
          )}
        </div>

        {planting.actualHarvestDate && (
          <div className="flex items-center text-sm">
            <span className="text-lg mr-2">✅</span>
            <div className="flex-1">
              <p className="text-gray-600 dark:text-gray-400">Actual Harvest</p>
              <p className="font-medium text-gray-900 dark:text-white">
                {formatDate(planting.actualHarvestDate)}
              </p>
            </div>
          </div>
        )}
      </div>

      {/* Actions */}
      <div className="flex gap-2 pt-4 border-t border-gray-200 dark:border-gray-700">
        <button
          onClick={() => onView(planting)}
          className="flex-1 px-4 py-2 bg-green-600 dark:bg-green-700 text-white rounded-md hover:bg-green-700 dark:hover:bg-green-600 transition text-sm font-medium"
        >
          View Details
        </button>
        {planting.status !== 'HARVESTED' && (
          <>
            <button
              onClick={() => onEdit(planting)}
              className="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 transition text-sm font-medium"
            >
              Edit
            </button>
            {planting.status === 'GROWING' && (
              <button
                onClick={() => onHarvest(planting)}
                className="px-4 py-2 bg-yellow-600 dark:bg-yellow-700 text-white rounded-md hover:bg-yellow-700 dark:hover:bg-yellow-600 transition text-sm font-medium"
              >
                Harvest
              </button>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default PlantingCard;