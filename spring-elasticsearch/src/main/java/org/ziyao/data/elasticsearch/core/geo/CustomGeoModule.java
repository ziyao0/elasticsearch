package org.ziyao.data.elasticsearch.core.geo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.geo.Point;

import java.io.IOException;

class PointSerializer extends JsonSerializer<Point> {
	@Override
	public void serialize(Point value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeObject(GeoPoint.fromPoint(value));
	}
}

class PointDeserializer extends JsonDeserializer<Point> {
	@Override
	public Point deserialize(JsonParser p, DeserializationContext context) throws IOException {
		return GeoPoint.toPoint(p.readValueAs(GeoPoint.class));
	}
}
