import React from 'react';

function FertilizerCard({ application, onViewRecommendation, onMarkCompleted, onPostpone, onSkip }) {
  const getUrgencyColor = (urgency) => {
    const colors = {
      CRITICAL: 'bg-red-500 dark:bg-red-600',
      HIGH: 'bg-orange-500 dark:bg-orange-600',
      MEDIUM: 'bg-yellow-500 dark:bg-yellow-600',
      LOW: 'bg-green-500 dark:bg-green-600',
    };
    return colors[urgency] || 'bg-gray-500';
  };

  const getUrgencyBorderColor = (urgency) => {
    const colors = {
      CRITICAL: 'border-red-500 dark:border-red-400',
      HIGH: 'border-orange-500 dark:border-orange-400',
      MEDIUM: 'border-yellow-500 dark:border-yellow-400',
      LOW: 'border-green-500 dark:border-green-400',
    };
    return colors[urgency] || 'border-gray-500';
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

  const getStatusColor = (status) => {
    const colors = {
      PENDING: 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300',
      SCHEDULED: 'bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300',
      COMPLETED: 'bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300',
      POSTPONED: 'bg-yellow-100 dark:bg-yellow-900 text-yellow-700 dark:text-yellow-300',
      SKIPPED: 'bg-red-100 dark:bg-red-900 text-red-700 dark:text-red-300',
    };
    return colors[status] || 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300';
  };

  const getStatusIcon = (status) => {
    const icons = {
      PENDING: '⏳',
      SCHEDULED: '📅',
      COMPLETED: '✅',
      POSTPONED: '⏸️',
      SKIPPED: '⏭️',
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

  const getDaysUntilApplication = () => {
    if (!application.scheduledDate || application.status === 'COMPLETED' || application.status === 'SKIPPED') {
      return null;
    }

    const today = new Date();
    const scheduledDate = new Date(application.scheduledDate);
    const diffTime = scheduledDate - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays < -5) return { text: `${Math.abs(diffDays)} days overdue!`, color: 'text-red-600 dark:text-red-400', urgent: true };
    if (diffDays < 0) return { text: `${Math.abs(diffDays)} days late`, color: 'text-orange-600 dark:text-orange-400', urgent: true };
    if (diffDays === 0) return { text: 'Due TODAY!', color: 'text-yellow-600 dark:text-yellow-400', urgent: false };
    if (diffDays === 1) return { text: 'Due tomorrow', color: 'text-blue-600 dark:text-blue-400', urgent: false };
    return { text: `In ${diffDays} days`, color: 'text-gray-600 dark:text-gray-400', urgent: false };
  };

  const daysInfo = getDaysUntilApplication();

  return (
    <div className={`bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-all p-6 border-l-4 ${getUrgencyBorderColor(application.urgency)}`}>
      {/* Header */}
      <div className="flex justify-between items-start mb-4">
        <div className="flex-1">
          <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-1">
            {application.fertilizerType || 'Fertilizer Application'}
          </h3>
          <p className="text-sm text-gray-600 dark:text-gray-400">
            {application.planting?.riceVariety?.name || 'Unknown Variety'} - {application.planting?.field?.name || 'Unknown Field'}
          </p>
        </div>
        <div className="flex flex-col items-end gap-2">
          <span className={`px-3 py-1 rounded-full text-xs font-bold ${getStatusColor(application.status)}`}>
            {getStatusIcon(application.status)} {application.status}
          </span>
          {application.urgency && application.status === 'PENDING' && (
            <span className={`px-3 py-1 rounded-full text-white text-xs font-bold ${getUrgencyColor(application.urgency)}`}>
              {getUrgencyIcon(application.urgency)} {application.urgency}
            </span>
          )}
        </div>
      </div>

      {/* Application Details */}
      <div className="space-y-3 mb-4">
        <div className="flex items-center text-sm">
          <span className="text-lg mr-2">📅</span>
          <div className="flex-1">
            <p className="text-gray-600 dark:text-gray-400">Scheduled Date</p>
            <p className="font-medium text-gray-900 dark:text-white">
              {formatDate(application.scheduledDate)}
            </p>
          </div>
          {daysInfo && (
            <span className={`text-sm font-bold ${daysInfo.color} ${daysInfo.urgent ? 'animate-pulse' : ''}`}>
              {daysInfo.text}
            </span>
          )}
        </div>

        {application.amount && (
          <div className="flex items-center text-sm">
            <span className="text-lg mr-2">⚖️</span>
            <div className="flex-1">
              <p className="text-gray-600 dark:text-gray-400">Amount</p>
              <p className="font-medium text-gray-900 dark:text-white">
                {application.amount} kg/ha
              </p>
            </div>
          </div>
        )}

        {application.actualApplicationDate && (
          <div className="flex items-center text-sm">
            <span className="text-lg mr-2">✅</span>
            <div className="flex-1">
              <p className="text-gray-600 dark:text-gray-400">Applied On</p>
              <p className="font-medium text-gray-900 dark:text-white">
                {formatDate(application.actualApplicationDate)}
              </p>
            </div>
          </div>
        )}

        {application.postponementReason && (
          <div className="flex items-center text-sm">
            <span className="text-lg mr-2">⏸️</span>
            <div className="flex-1">
              <p className="text-gray-600 dark:text-gray-400">Postponed Because</p>
              <p className="font-medium text-gray-900 dark:text-white">
                {application.postponementReason}
              </p>
            </div>
          </div>
        )}
      </div>

      {/* Weather Recommendation (if pending) */}
      {application.status === 'PENDING' && application.recommendation && (
        <div className={`p-3 rounded-lg mb-4 ${
          application.urgency === 'CRITICAL'
            ? 'bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800'
            : application.urgency === 'HIGH'
            ? 'bg-orange-50 dark:bg-orange-900/30 border border-orange-200 dark:border-orange-800'
            : application.urgency === 'MEDIUM'
            ? 'bg-yellow-50 dark:bg-yellow-900/30 border border-yellow-200 dark:border-yellow-800'
            : 'bg-green-50 dark:bg-green-900/30 border border-green-200 dark:border-green-800'
        }`}>
          <p className={`text-sm font-semibold mb-1 ${
            application.urgency === 'CRITICAL'
              ? 'text-red-900 dark:text-red-300'
              : application.urgency === 'HIGH'
              ? 'text-orange-900 dark:text-orange-300'
              : application.urgency === 'MEDIUM'
              ? 'text-yellow-900 dark:text-yellow-300'
              : 'text-green-900 dark:text-green-300'
          }`}>
            🌤️ Smart Recommendation:
          </p>
          <p className={`text-xs ${
            application.urgency === 'CRITICAL'
              ? 'text-red-800 dark:text-red-400'
              : application.urgency === 'HIGH'
              ? 'text-orange-800 dark:text-orange-400'
              : application.urgency === 'MEDIUM'
              ? 'text-yellow-800 dark:text-yellow-400'
              : 'text-green-800 dark:text-green-400'
          }`}>
            {application.recommendation}
          </p>
        </div>
      )}

      {/* Actions */}
      {application.status === 'PENDING' && (
        <div className="flex flex-wrap gap-2 pt-4 border-t border-gray-200 dark:border-gray-700">
          <button
            onClick={() => onViewRecommendation(application)}
            className="flex-1 min-w-[120px] px-3 py-2 bg-blue-600 dark:bg-blue-700 text-white rounded-md hover:bg-blue-700 dark:hover:bg-blue-600 transition text-sm font-medium"
          >
            📊 Full Analysis
          </button>
          <button
            onClick={() => onMarkCompleted(application)}
            className="flex-1 min-w-[120px] px-3 py-2 bg-green-600 dark:bg-green-700 text-white rounded-md hover:bg-green-700 dark:hover:bg-green-600 transition text-sm font-medium"
          >
            ✅ Mark Done
          </button>
          <button
            onClick={() => onPostpone(application)}
            className="px-3 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 transition text-sm font-medium"
          >
            ⏸️ Postpone
          </button>
          <button
            onClick={() => onSkip(application)}
            className="px-3 py-2 border border-red-300 dark:border-red-600 text-red-700 dark:text-red-300 rounded-md hover:bg-red-50 dark:hover:bg-red-900/30 transition text-sm font-medium"
          >
            ⏭️ Skip
          </button>
        </div>
      )}
    </div>
  );
}

export default FertilizerCard;