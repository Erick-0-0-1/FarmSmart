// Mock AI Soil Analysis Service
// This will be replaced with Google Vision API in production

const analyzeSoilMoisture = async (imageFile, weatherForecast) => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 2000));

  // Mock AI Analysis - In production, this will call Google Vision API
  // The API will analyze: moisture level, cracks, plant health, soil color
  const mockAnalysis = {
    // Soil moisture level: DRY, SLIGHTLY_DRY, OPTIMAL, WET, WATERLOGGED
    moistureLevel: getRandomMoistureLevel(),
    // Overall soil condition
    soilCondition: getRandomSoilCondition(),
    // Crack severity: NONE, MINOR, MODERATE, SEVERE
    crackSeverity: getRandomCrackSeverity(),
    // Plant stress level: NONE, LOW, MODERATE, HIGH, CRITICAL
    plantStress: getRandomPlantStress(),
    // Confidence score (0-100)
    confidence: Math.floor(Math.random() * 20) + 80, // 80-100%
  };

  // Analyze weather forecast
  const weatherAnalysis = analyzeWeatherForIrrigation(weatherForecast);

  // Generate smart recommendation
  const recommendation = generateSmartRecommendation(mockAnalysis, weatherAnalysis);

  return recommendation;
};

function getRandomMoistureLevel() {
  const levels = ['DRY', 'SLIGHTLY_DRY', 'OPTIMAL', 'WET'];
  return levels[Math.floor(Math.random() * levels.length)];
}

function getRandomSoilCondition() {
  const conditions = ['Dry and cracked', 'Slightly dry', 'Good condition', 'Moist', 'Wet'];
  return conditions[Math.floor(Math.random() * conditions.length)];
}

function getRandomCrackSeverity() {
  const severities = ['None', 'Minor', 'Moderate', 'Severe'];
  return severities[Math.floor(Math.random() * severities.length)];
}

function getRandomPlantStress() {
  const stressLevels = ['None', 'Low', 'Moderate', 'High'];
  return stressLevels[Math.floor(Math.random() * stressLevels.length)];
}

function analyzeWeatherForIrrigation(forecast) {
  if (!forecast || forecast.length === 0) {
    return {
      rainComing: false,
      daysUntilRain: null,
      totalRainfall: 0,
      rainyDays: 0,
    };
  }

  let daysUntilRain = null;
  let totalRainfall = 0;
  let rainyDays = 0;

  forecast.forEach((day, index) => {
    if (day.totalRainfall > 5) { // Significant rain (>5mm)
      rainyDays++;
      totalRainfall += day.totalRainfall;
      if (daysUntilRain === null) {
        daysUntilRain = index;
      }
    }
  });

  return {
    rainComing: rainyDays > 0,
    daysUntilRain,
    totalRainfall,
    rainyDays,
  };
}

function generateSmartRecommendation(soilAnalysis, weatherAnalysis) {
  const { moistureLevel, soilCondition, crackSeverity, plantStress } = soilAnalysis;
  const { rainComing, daysUntilRain, totalRainfall, rainyDays } = weatherAnalysis;

  let urgency, recommendation, detailedAdvice, weatherContext, actionTiming;

  // CRITICAL CASES
  if (moistureLevel === 'DRY' && crackSeverity === 'Severe' && !rainComing) {
    urgency = 'CRITICAL';
    recommendation = '🚨 URGENT: Irrigate Immediately!';
    detailedAdvice = 'Your soil is severely dry with deep cracks and no rain is forecasted. Immediate irrigation is critical to prevent crop damage and yield loss.';
    weatherContext = 'No significant rainfall expected in the next 7 days.';
    actionTiming = 'Irrigate NOW - your crops are in critical condition.';
  }
  else if (moistureLevel === 'DRY' && plantStress === 'High' && daysUntilRain > 3) {
    urgency = 'CRITICAL';
    recommendation = '🚨 URGENT: Water Needed Now!';
    detailedAdvice = 'Plants are showing high stress and rain is still days away. Delaying irrigation will result in significant crop damage.';
    weatherContext = `Light rain expected in ${daysUntilRain} days, but it's not enough.`;
    actionTiming = 'Start irrigation today - don\'t wait for the rain.';
  }

  // HIGH URGENCY CASES
  else if (moistureLevel === 'DRY' && !rainComing) {
    urgency = 'HIGH';
    recommendation = '⚠️ Irrigation Recommended Soon';
    detailedAdvice = 'Soil moisture is low and no rain is forecasted. Plan to irrigate within the next 1-2 days to maintain crop health.';
    weatherContext = 'Dry weather expected for the next 7 days.';
    actionTiming = 'Irrigate within 1-2 days.';
  }
  else if (moistureLevel === 'SLIGHTLY_DRY' && daysUntilRain > 4) {
    urgency = 'HIGH';
    recommendation = '⚠️ Light Irrigation Needed';
    detailedAdvice = 'Soil is starting to dry out and rain is still several days away. Light irrigation will help maintain optimal growing conditions.';
    weatherContext = `Some rain expected in ${daysUntilRain} days, but soil will be too dry by then.`;
    actionTiming = 'Apply light irrigation in the next 2-3 days.';
  }

  // MEDIUM URGENCY CASES
  else if (moistureLevel === 'SLIGHTLY_DRY' && rainComing && daysUntilRain <= 2) {
    urgency = 'MEDIUM';
    recommendation = '💧 Monitor - Rain Coming Soon';
    detailedAdvice = 'Soil is slightly dry but rain is expected within 2 days. Monitor your crops and wait for the rain unless they show stress.';
    weatherContext = `Rain expected in ${daysUntilRain} ${daysUntilRain === 1 ? 'day' : 'days'} (${totalRainfall.toFixed(0)}mm total).`;
    actionTiming = 'Wait for rain, but monitor crops closely.';
  }
  else if (moistureLevel === 'DRY' && rainComing && daysUntilRain === 1) {
    urgency = 'MEDIUM';
    recommendation = '💧 Light Watering Optional';
    detailedAdvice = 'Soil is dry but significant rain is expected tomorrow. You may apply light irrigation today if crops show stress, or wait for the rain.';
    weatherContext = 'Good rainfall expected tomorrow.';
    actionTiming = 'Optional: Light watering today, or wait for tomorrow\'s rain.';
  }

  // LOW URGENCY CASES (GOOD CONDITIONS)
  else if (moistureLevel === 'OPTIMAL') {
    urgency = 'LOW';
    recommendation = '✅ Perfect! No Irrigation Needed';
    detailedAdvice = 'Soil moisture is at optimal levels for rice growth. Continue monitoring but no action needed right now.';
    weatherContext = rainComing
      ? `Rain coming in ${daysUntilRain} days will help maintain good moisture.`
      : 'Keep monitoring soil moisture levels.';
    actionTiming = 'No irrigation needed. Check again in 2-3 days.';
  }
  else if (moistureLevel === 'WET') {
    urgency = 'LOW';
    recommendation = '✅ Excellent Moisture Levels';
    detailedAdvice = 'Soil has plenty of moisture. No irrigation needed. Ensure proper drainage to prevent waterlogging.';
    weatherContext = rainComing
      ? `More rain expected - monitor drainage to prevent waterlogging.`
      : 'Soil moisture is excellent for rice growth.';
    actionTiming = 'No irrigation needed. Monitor drainage if more rain comes.';
  }
  else if (moistureLevel === 'SLIGHTLY_DRY' && rainComing && daysUntilRain <= 3) {
    urgency = 'LOW';
    recommendation = '✅ Wait for Rain';
    detailedAdvice = 'Soil is slightly dry but adequate rain is forecasted soon. No irrigation needed - let nature do the work.';
    weatherContext = `Good rain expected in ${daysUntilRain} days (${totalRainfall.toFixed(0)}mm).`;
    actionTiming = 'No action needed - rain will replenish soil moisture.';
  }

  // DEFAULT CASE
  else {
    urgency = 'MEDIUM';
    recommendation = '💧 Monitor Soil Condition';
    detailedAdvice = 'Keep monitoring your soil and crop condition. Take action if you notice plant stress or further soil drying.';
    weatherContext = rainComing
      ? `Some rain expected in ${daysUntilRain} days.`
      : 'Weather conditions are variable.';
    actionTiming = 'Check soil condition daily and irrigate if needed.';
  }

  return {
    urgency,
    recommendation,
    detailedAdvice,
    weatherContext,
    actionTiming,
    moistureLevel: formatMoistureLevel(moistureLevel),
    soilCondition,
    crackSeverity,
    plantStress,
    confidence: soilAnalysis.confidence,
  };
}

function formatMoistureLevel(level) {
  const formats = {
    'DRY': 'Dry (20-30%)',
    'SLIGHTLY_DRY': 'Slightly Dry (40-50%)',
    'OPTIMAL': 'Optimal (60-70%)',
    'WET': 'Wet (80-90%)',
    'WATERLOGGED': 'Waterlogged (>95%)',
  };
  return formats[level] || level;
}

export { analyzeSoilMoisture };