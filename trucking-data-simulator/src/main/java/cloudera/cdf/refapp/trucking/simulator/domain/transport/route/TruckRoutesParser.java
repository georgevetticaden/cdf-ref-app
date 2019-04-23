package cloudera.cdf.refapp.trucking.simulator.domain.transport.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudera.cdf.refapp.trucking.simulator.domain.gps.Location;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.route.jaxb.Kml;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.route.jaxb.Placemark;
import cloudera.cdf.refapp.trucking.simulator.util.DataGeneratorUtils;





public class TruckRoutesParser {
	
	private static final Logger LOG = LoggerFactory.getLogger(TruckRoutesParser.class);
	private static final DecimalFormat numberFormat = new DecimalFormat("#.00");
	private Map<String, Integer> routeIds = new HashMap<String, Integer>();

	public Route parseRoute(String routeFile) {
        LOG.info("Processing Route File["+routeFile+"]");
		Route route = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(Kml.class);
			Unmarshaller u = jc.createUnmarshaller();
			Source source = new StreamSource(new FileInputStream(routeFile));
			
			JAXBElement<Kml> root =  u.unmarshal(source, Kml.class );
			
			Kml kml = root.getValue();
			
			String routeName = kml.getDocument().getName();
			List<Location> locations = new ArrayList<Location>();
			//-74.1346263885498,40.63616666172068,0.0
			for(Placemark placemark:kml.getDocument().getPlacemark()) {
				String coordinates = placemark.getPoint().getCoordinates();
				String[] coord = coordinates.split(",");
				String latitude = numberFormat.format(Double.valueOf(coord[0]));
				String longitude = numberFormat.format(Double.valueOf(coord[1]));
				locations.add(new Location(Double.valueOf(latitude), Double.valueOf(longitude), 0));
			}
			LOG.info("Route File["+routeFile +"] has " + locations.size() + " coordinates in the route ");
			int routeId = getRouteId(routeName);
			route = new RouteProvided(routeId, routeName, locations);
		} catch (FileNotFoundException e) {
			String errorMessage = "Error Opening routeFile["+routeFile+"]";
			LOG.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		} catch (JAXBException e) {
			String errorMessage = "JaxB exception for routeFile"+routeFile+"]";
			LOG.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
		return route;
	}
	
	private Integer getRouteId(String routeName) {
		Integer routeId = routeIds.get(routeName);
		if(routeId == null) {
			routeId = DataGeneratorUtils.getRandomInt(15, true);
			routeIds.put(routeName, routeId);
		}
		return routeId;
		
	}

	public List<Route> parseAllRoutes(String directoryName) {
		List<Route> routes = new ArrayList<>();
		File directory = new File(directoryName);
		File[] files =  directory.listFiles();
		Arrays.sort(files);
		
		for(File routeFile: files) {
			if(routeFile.getPath().endsWith(".kml")) {
				routes.add(parseRoute(routeFile.getPath()));
			}
		}
		return routes;
	}
}
