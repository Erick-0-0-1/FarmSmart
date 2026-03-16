import React from 'react';
import { useLanguage } from '../context/LanguageContext';

function FieldCard({ field, onView, onEdit, onDelete }) {
  const { t } = useLanguage();

  return (
    <div className="bg-white dark:bg-gray-700 rounded-lg shadow-md hover:shadow-lg transition-shadow p-6 border border-gray-200 dark:border-gray-600">
      {/* Field Name */}
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-xl font-bold text-gray-900 dark:text-white">{field.name}</h3>
        <span className="px-2 py-1 bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300 text-xs rounded-full">
          Active
        </span>
      </div>

      {/* Field Details */}
      <div className="space-y-2 mb-4">
        <div className="flex items-center text-gray-600 dark:text-gray-300">
          <span className="text-lg mr-2">📏</span>
          <span className="text-sm">
            {field.areaHectares} {t('area')}
          </span>
        </div>

        <div className="flex items-center text-gray-600 dark:text-gray-300">
          <span className="text-lg mr-2">📍</span>
          <span className="text-sm">{field.location}</span>
        </div>

        {field.soilType && (
          <div className="flex items-center text-gray-600 dark:text-gray-300">
            <span className="text-lg mr-2">🌱</span>
            <span className="text-sm">{field.soilType}</span>
          </div>
        )}

        <div className="flex items-center text-gray-600 dark:text-gray-300">
          <span className="text-lg mr-2">💧</span>
          <span className="text-sm">{field.irrigationType}</span>
        </div>
      </div>

      {/* Actions */}
      <div className="flex gap-2 pt-4 border-t border-gray-200 dark:border-gray-600">
        <button
          onClick={() => onView(field)}
          className="flex-1 px-4 py-2 bg-green-600 dark:bg-green-700 text-white rounded-md hover:bg-green-700 dark:hover:bg-green-600 transition text-sm font-medium"
        >
          {t('view')}
        </button>
        <button
          onClick={() => onEdit(field)}
          className="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-md hover:bg-gray-50 dark:hover:bg-gray-600 transition text-sm font-medium"
        >
          {t('edit')}
        </button>
      </div>
    </div>
  );
}

export default FieldCard;