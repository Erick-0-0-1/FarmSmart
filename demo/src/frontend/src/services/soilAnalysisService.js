// Upgraded Mock AI - Analyzes actual image properties for realism
const analyzeSoilMoisture = async (imageFile, weatherForecast) => {
  return new Promise((resolve) => {
    // Simulate AI processing time
    setTimeout(() => {
      const reader = new FileReader();

      reader.onload = (e) => {
        const img = new Image();
        img.onload = () => {
          try {
            // Create canvas to analyze image
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            canvas.width = img.width;
            canvas.height = img.height;
            ctx.drawImage(img, 0, 0);

            // Get image data for analysis
            const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
            const data = imageData.data;

            // Check if we have valid image data
            if (!data || data.length === 0) {
              resolve({
                urgency: 'ERROR',
                recommendation: 'Failed to analyze image',
                detailedAdvice: 'Please upload a valid image file',
                actionTiming: 'Try again with a different image',
                moistureLevel: 'UNKNOWN',
                soilCondition: 'Unable to analyze',
                crackSeverity: 'UNKNOWN',
                plantStress: 'UNKNOWN',
                confidence: 0
              });
              return;
            }

            // Calculate average brightness and color
            let totalBrightness = 0;
            let totalRed = 0;
            let totalGreen = 0;
            let totalBlue = 0;

            for (let i = 0; i < data.length; i += 4) {
              totalRed += data[i];
              totalGreen += data[i + 1];
              totalBlue += data[i + 2];
              totalBrightness += (data[i] + data[i + 1] + data[i + 2]) / 3;
            }

            const pixelCount = data.length / 4;
            const avgBrightness = totalBrightness / pixelCount;
            const avgRed = totalRed / pixelCount;
            const avgGreen = totalGreen / pixelCount;
            const avgBlue = totalBlue / pixelCount;
            const avgBrown = (avgRed * 0.6 + avgGreen * 0.3 + avgBlue * 0.1); // Brown soil indicator

            // SOIL DETECTION CHECK - Improved logic
            const isSoil = detectSoilImage(avgRed, avgGreen, avgBlue, avgBrightness);

            if (!isSoil) {
              resolve({
                urgency: 'ERROR',
                recommendation: '❌ This doesn\'t look like soil!',
                detailedAdvice: 'The uploaded image doesn\'t appear to be soil. Please upload a clear photo of soil or a farm field. Soil images are typically brown, dark, or earthy in color.',
                actionTiming: 'Try again with a soil photo',
                moistureLevel: 'UNKNOWN',
                soilCondition: 'Not soil detected',
                crackSeverity: 'UNKNOWN',
                plantStress: 'UNKNOWN',
                confidence: 0,
                isSoil: false,
                colorAnalysis: {
                  avgRed: Math.round(avgRed),
                  avgGreen: Math.round(avgGreen),
                  avgBlue: Math.round(avgBlue),
                  avgBrown: Math.round(avgBrown),
                  avgBrightness: Math.round(avgBrightness)
                }
              });
              return;
            }

            // Analyze soil based on image properties
            const analysis = analyzeImageProperties(avgBrightness, avgBrown, avgGreen);

            // Combine with weather forecast for smart recommendations
            const recommendation = generateSmartRecommendation(analysis, weatherForecast);

            // Add soil confirmation to response
            resolve({
              ...recommendation,
              isSoil: true,
              colorAnalysis: {
                avgRed: Math.round(avgRed),
                avgGreen: Math.round(avgGreen),
                avgBlue: Math.round(avgBlue),
                avgBrown: Math.round(avgBrown),
                avgBrightness: Math.round(avgBrightness)
              }
            });
          } catch (error) {
            console.error('Error analyzing soil moisture:', error);
            resolve({
              urgency: 'ERROR',
              recommendation: 'Analysis failed',
              detailedAdvice: 'An error occurred while analyzing the image. Please try again.',
              actionTiming: 'Try again',
              moistureLevel: 'UNKNOWN',
              soilCondition: 'Analysis error',
              crackSeverity: 'UNKNOWN',
              plantStress: 'UNKNOWN',
              confidence: 0,
              error: error.message
            });
          }
        };

        img.onerror = () => {
          resolve({
            urgency: 'ERROR',
            recommendation: 'Invalid image file',
            detailedAdvice: 'The uploaded file is not a valid image. Please upload a JPG, PNG, or WebP image.',
            actionTiming: 'Try again with a different file',
            moistureLevel: 'UNKNOWN',
            soilCondition: 'Invalid image',
            crackSeverity: 'UNKNOWN',
            plantStress: 'UNKNOWN',
            confidence: 0
          });
        };

        img.src = e.target.result;
      };

      reader.onerror = () => {
        resolve({
          urgency: 'ERROR',
          recommendation: 'Failed to read file',
          detailedAdvice: 'Could not read the uploaded file. Please try again.',
          actionTiming: 'Try again',
          moistureLevel: 'UNKNOWN',
          soilCondition: 'File read error',
          crackSeverity: 'UNKNOWN',
          plantStress: 'UNKNOWN',
          confidence: 0
        });
      };

      reader.readAsDataURL(imageFile);
    }, 2000); // 2 second "processing" time
  });
};

// SOIL DETECTION FUNCTION - Improved logic
const detectSoilImage = (avgRed, avgGreen, avgBlue, avgBrightness) => {
  // Soil typically has these characteristics:
  // 1. Brownish color (red > green, red > blue)
  // 2. Not too blue or too green (soil isn't sky or grass)
  // 3. Reasonable brightness range (not pure white or black)

  // Calculate color ratios
  const redToGreenRatio = avgRed / avgGreen;
  const redToBlueRatio = avgRed / avgBlue;
  const colorSaturation = (Math.max(avgRed, avgGreen, avgBlue) - Math.min(avgRed, avgGreen, avgBlue)) / 255;

  // SOIL DETECTION RULES:

  // Rule 1: Soil is usually brown - red should be highest or close to highest
  // Typical soil has R > G > B or R > B > G
  const isBrownish = (avgRed > avgGreen * 0.9) && (avgRed > avgBlue * 0.9);

  // Rule 2: Soil shouldn't be too blue (like sky/water) or too green (like grass)
  const notTooBlue = avgBlue < 150; // Soil rarely has high blue component
  const notTooGreen = avgGreen < 180; // Very green might be grass

  // Rule 3: Soil has earthy tones - check for brownish hue
  // Brown typically has R > 100, G between 50-150, B < 100
  const hasEarthyTones = avgRed > 80 && avgRed < 220 &&
                        avgGreen > 40 && avgGreen < 180 &&
                        avgBlue < 120;

  // Rule 4: Check color saturation - soil usually has moderate saturation
  const hasModerateSaturation = colorSaturation > 0.1 && colorSaturation < 0.6;

  // Rule 5: Brightness check - soil is usually not too bright or too dark
  const reasonableBrightness = avgBrightness > 50 && avgBrightness < 200;

  // Rule 6: Color dominance - red should be dominant in soil
  const redDominant = avgRed > avgGreen + 10 && avgRed > avgBlue + 10;

  // Combine rules with weights
  let soilScore = 0;

  if (isBrownish) soilScore += 2;
  if (notTooBlue) soilScore += 1;
  if (notTooGreen) soilScore += 1;
  if (hasEarthyTones) soilScore += 2;
  if (hasModerateSaturation) soilScore += 1;
  if (reasonableBrightness) soilScore += 1;
  if (redDominant) soilScore += 2;

  // Also check for common non-soil colors
  const isTooGray = colorSaturation < 0.1; // Grayscale images
  const isTooBlueSky = avgBlue > avgRed + 50 && avgBlue > avgGreen + 50;
  const isTooGreenGrass = avgGreen > avgRed + 30 && avgGreen > avgBlue + 30;
  const isWhiteScreen = avgBrightness > 230 && colorSaturation < 0.2;
  const isBlackScreen = avgBrightness < 30;

  // Penalize non-soil characteristics
  if (isTooGray || isTooBlueSky || isTooGreenGrass || isWhiteScreen || isBlackScreen) {
    soilScore -= 3;
  }

  console.log('Soil detection analysis:', {
    avgRed: Math.round(avgRed),
    avgGreen: Math.round(avgGreen),
    avgBlue: Math.round(avgBlue),
    brightness: Math.round(avgBrightness),
    saturation: colorSaturation.toFixed(2),
    soilScore,
    isBrownish,
    notTooBlue,
    notTooGreen,
    hasEarthyTones,
    redDominant,
    isTooGray,
    isTooBlueSky,
    isTooGreenGrass,
    isWhiteScreen,
    isBlackScreen
  });

  // Minimum score to consider as soil
  return soilScore >= 6;
};

// Analyze image properties to determine soil condition
const analyzeImageProperties = (brightness, brownness, greenness) => {
  let moistureLevel, crackSeverity, plantStress, soilCondition;

  // Determine moisture based on brightness (darker = wetter)
  if (brightness < 80) {
    moistureLevel = 'WET';
    soilCondition = 'Saturated soil detected';
  } else if (brightness < 120) {
    moistureLevel = 'OPTIMAL';
    soilCondition = 'Good moisture level';
  } else if (brightness < 160) {
    moistureLevel = 'SLIGHTLY_DRY';
    soilCondition = 'Moderate moisture deficit';
  } else {
    moistureLevel = 'DRY';
    soilCondition = 'Severe moisture deficit';
  }

  // Determine cracks based on brightness variation (bright = cracks visible)
  if (brightness > 180) {
    crackSeverity = 'SEVERE';
  } else if (brightness > 150) {
    crackSeverity = 'MODERATE';
  } else if (brightness > 120) {
    crackSeverity = 'MINOR';
  } else {
    crackSeverity = 'NONE';
  }

  // Determine plant stress based on green content
  if (greenness < 60) {
    plantStress = 'HIGH';
  } else if (greenness < 90) {
    plantStress = 'MODERATE';
  } else if (greenness < 120) {
    plantStress = 'LOW';
  } else {
    plantStress = 'NONE';
  }

  // Calculate confidence (higher for more extreme conditions)
  const confidence = Math.min(95, 75 + Math.abs(brightness - 128) / 5);

  return {
    moistureLevel,
    soilCondition,
    crackSeverity,
    plantStress,
    confidence: Math.round(confidence)
  };
};

// Generate smart recommendation based on soil analysis + weather
const generateSmartRecommendation = (analysis, forecast) => {
  const { moistureLevel, crackSeverity, plantStress } = analysis;

  // Add this after the generateSmartRecommendation function

  // Rice-specific moisture recommendations based on growth stage
  const getRiceStageRecommendation = (moistureLevel, growthStage) => {
    const recommendations = {
      VEGETATIVE: {
        OPTIMAL: 'WET to OPTIMAL',
        advice: 'Keep field flooded 5-10cm deep during vegetative stage'
      },
      REPRODUCTIVE: {
        OPTIMAL: 'OPTIMAL to SLIGHTLY_DRY',
        advice: 'Maintain saturated soil, reduce flooding during flowering'
      },
      MATURATION: {
        OPTIMAL: 'SLIGHTLY_DRY to DRY',
        advice: 'Drain field 7-14 days before harvest for easier harvesting'
      },
      HARVEST_READY: {
        OPTIMAL: 'DRY',
        advice: 'Field should be completely dry for harvesting equipment'
      }
    };

    return recommendations[growthStage] || recommendations.VEGETATIVE;
  };

  // Analyze weather forecast
  const rainyDays = forecast ? forecast.filter(day =>
    (day.rainfall || day.totalRainfall || 0) > 5
  ).length : 0;

  const rainToday = forecast && forecast[0] ?
    (forecast[0].rainfall || forecast[0].totalRainfall || 0) > 5 : false;

  const rainTomorrow = forecast && forecast[1] ?
    (forecast[1].rainfall || forecast[1].totalRainfall || 0) > 5 : false;

  const rainSoon = forecast && forecast.slice(0, 3).some(day =>
    (day.rainfall || day.totalRainfall || 0) > 5
  );

  // Decision tree for smart recommendations
  let urgency, recommendation, detailedAdvice, actionTiming;

  // CRITICAL CONDITIONS
  if (moistureLevel === 'DRY' && crackSeverity === 'SEVERE' && plantStress === 'HIGH') {
    urgency = 'CRITICAL';
    recommendation = '🚨 URGENT: Emergency irrigation needed NOW!';
    detailedAdvice = 'Soil is severely dry with visible cracks and plants showing high stress. This is a critical situation that requires immediate action to prevent crop damage. Apply deep irrigation immediately, even if rain is forecasted.';
    actionTiming = 'Irrigate immediately - crop survival at risk';
  }
  else if (moistureLevel === 'DRY' && !rainSoon) {
    urgency = 'CRITICAL';
    recommendation = '🚨 Apply irrigation NOW - No rain forecasted';
    detailedAdvice = 'Soil moisture is critically low and no rainfall expected in the next 3 days. Immediate irrigation is essential to prevent crop stress and yield loss.';
    actionTiming = 'Irrigate today - do not delay';
  }

  // HIGH PRIORITY CONDITIONS
  else if (moistureLevel === 'DRY' && rainTomorrow) {
    urgency = 'MEDIUM';
    recommendation = '💧 Light irrigation recommended - Rain expected tomorrow';
    detailedAdvice = 'Soil is dry but rain is forecasted tomorrow. Consider light irrigation today to help plants until rain arrives, or wait if crops can tolerate one more day.';
    actionTiming = 'Optional light watering today, or wait for rain tomorrow';
  }
  else if (moistureLevel === 'SLIGHTLY_DRY' && !rainSoon) {
    urgency = 'HIGH';
    recommendation = '⚠️ Plan irrigation within 24-48 hours';
    detailedAdvice = 'Soil moisture is declining and no rain expected soon. Plan to irrigate in the next 1-2 days to maintain optimal growing conditions.';
    actionTiming = 'Irrigate within 48 hours';
  }

  // MEDIUM PRIORITY CONDITIONS
  else if (moistureLevel === 'SLIGHTLY_DRY' && rainSoon) {
    urgency = 'LOW';
    recommendation = '✅ Monitor - Rain expected soon';
    detailedAdvice = 'Soil is slightly dry but rain is forecasted within 3 days. Continue monitoring, but irrigation likely not needed.';
    actionTiming = 'Wait for forecasted rain, monitor soil condition';
  }

  // OPTIMAL CONDITIONS
  else if (moistureLevel === 'OPTIMAL') {
    urgency = 'LOW';
    recommendation = '✅ Perfect moisture! No irrigation needed';
    detailedAdvice = 'Soil moisture is at optimal levels for crop growth. Continue regular monitoring and maintain current irrigation schedule.';
    actionTiming = 'No action needed - conditions are ideal';
  }

  // WET CONDITIONS
  else if (moistureLevel === 'WET') {
    urgency = 'LOW';
    recommendation = '💦 Soil is saturated - Ensure proper drainage';
    detailedAdvice = 'Soil has high moisture content. Check drainage systems and avoid additional irrigation until soil moisture decreases.';
    actionTiming = 'No irrigation needed - monitor for drainage issues';
  }

  // FALLBACK
  else {
    urgency = 'MEDIUM';
    recommendation = '💧 Monitor soil conditions closely';
    detailedAdvice = 'Current conditions warrant close monitoring. Check soil moisture daily and adjust irrigation based on weather and crop needs.';
    actionTiming = 'Continue monitoring, be prepared to irrigate';
  }

  return {
    urgency,
    recommendation,
    detailedAdvice,
    actionTiming,
    moistureLevel,
    soilCondition: analysis.soilCondition,
    crackSeverity,
    plantStress,
    confidence: analysis.confidence,
    weatherContext: generateWeatherContext(rainyDays, rainToday, rainTomorrow, rainSoon),
    rainyDaysCount: rainyDays,
    analysisDate: new Date().toISOString()
  };
};

// Generate weather context explanation
const generateWeatherContext = (rainyDays, rainToday, rainTomorrow, rainSoon) => {
  if (rainToday) {
    return 'Heavy rain today - irrigation not recommended';
  } else if (rainTomorrow) {
    return 'Rain forecasted tomorrow - consider waiting';
  } else if (rainSoon) {
    return `${rainyDays} rainy day(s) expected in next 3 days`;
  } else if (rainyDays > 0) {
    return `Light rain possible in the coming week (${rainyDays} day(s))`;
  } else {
    return 'No significant rainfall expected in the forecast period';
  }
};

// Simple fallback function for testing without image upload
const mockAnalyzeSoilMoisture = (weatherForecast) => {
  return {
    urgency: 'MEDIUM',
    recommendation: '💧 Monitor soil conditions closely',
    detailedAdvice: 'Current conditions warrant close monitoring. Check soil moisture daily and adjust irrigation based on weather and crop needs.',
    actionTiming: 'Continue monitoring, be prepared to irrigate',
    moistureLevel: 'SLIGHTLY_DRY',
    soilCondition: 'Moderate moisture deficit',
    crackSeverity: 'MINOR',
    plantStress: 'LOW',
    confidence: 75,
    weatherContext: 'No significant rainfall expected in the forecast period',
    rainyDaysCount: 0,
    analysisDate: new Date().toISOString(),
    isSoil: true
  };
};

export { analyzeSoilMoisture, mockAnalyzeSoilMoisture };