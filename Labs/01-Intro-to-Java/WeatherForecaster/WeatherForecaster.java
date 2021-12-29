
public class WeatherForecaster {

    public static int[] getsWarmerIn(int[] temperatures) {
        int[] stackIndices = new int[temperatures.length];
        int[] forecast = new int[temperatures.length];
        int tos = -1;
        
        for (int i = 0; i < temperatures.length; i++) {

            int currTemp = temperatures[i];
            // Stack is empty
            if (tos == -1) {
                ++tos;
                stackIndices[tos] = i;
                continue;
	    }

	    int tosVal = temperatures[stackIndices[tos]];

	    while (currTemp > tosVal) {

		    forecast[stackIndices[tos]] = i - stackIndices[tos];

		    --tos;

		    if (tos != -1) {
			    tosVal = temperatures[stackIndices[tos]];
		    } else {
			    break;
		    }
	    }

	    ++tos;
	    stackIndices[tos] = i;
	}

	return forecast;
    }

}
