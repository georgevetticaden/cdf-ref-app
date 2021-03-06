/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package cloudera.cdf.csp.schema.refapp.trucking;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class TruckSpeedEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 5216761773974671207L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TruckSpeedEvent\",\"namespace\":\"cloudera.cdf.csp.schema.refapp.trucking\",\"fields\":[{\"name\":\"eventTime\",\"type\":\"string\"},{\"name\":\"eventTimeLong\",\"type\":\"long\",\"default\":0},{\"name\":\"eventSource\",\"type\":\"string\"},{\"name\":\"truckId\",\"type\":\"int\"},{\"name\":\"driverId\",\"type\":\"int\"},{\"name\":\"driverName\",\"type\":\"string\"},{\"name\":\"routeId\",\"type\":\"int\"},{\"name\":\"route\",\"type\":\"string\"},{\"name\":\"speed\",\"type\":\"int\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<TruckSpeedEvent> ENCODER =
      new BinaryMessageEncoder<TruckSpeedEvent>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<TruckSpeedEvent> DECODER =
      new BinaryMessageDecoder<TruckSpeedEvent>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<TruckSpeedEvent> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<TruckSpeedEvent> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<TruckSpeedEvent>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this TruckSpeedEvent to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a TruckSpeedEvent from a ByteBuffer. */
  public static TruckSpeedEvent fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.CharSequence eventTime;
  @Deprecated public long eventTimeLong;
  @Deprecated public java.lang.CharSequence eventSource;
  @Deprecated public int truckId;
  @Deprecated public int driverId;
  @Deprecated public java.lang.CharSequence driverName;
  @Deprecated public int routeId;
  @Deprecated public java.lang.CharSequence route;
  @Deprecated public int speed;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public TruckSpeedEvent() {}

  /**
   * All-args constructor.
   * @param eventTime The new value for eventTime
   * @param eventTimeLong The new value for eventTimeLong
   * @param eventSource The new value for eventSource
   * @param truckId The new value for truckId
   * @param driverId The new value for driverId
   * @param driverName The new value for driverName
   * @param routeId The new value for routeId
   * @param route The new value for route
   * @param speed The new value for speed
   */
  public TruckSpeedEvent(java.lang.CharSequence eventTime, java.lang.Long eventTimeLong, java.lang.CharSequence eventSource, java.lang.Integer truckId, java.lang.Integer driverId, java.lang.CharSequence driverName, java.lang.Integer routeId, java.lang.CharSequence route, java.lang.Integer speed) {
    this.eventTime = eventTime;
    this.eventTimeLong = eventTimeLong;
    this.eventSource = eventSource;
    this.truckId = truckId;
    this.driverId = driverId;
    this.driverName = driverName;
    this.routeId = routeId;
    this.route = route;
    this.speed = speed;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return eventTime;
    case 1: return eventTimeLong;
    case 2: return eventSource;
    case 3: return truckId;
    case 4: return driverId;
    case 5: return driverName;
    case 6: return routeId;
    case 7: return route;
    case 8: return speed;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: eventTime = (java.lang.CharSequence)value$; break;
    case 1: eventTimeLong = (java.lang.Long)value$; break;
    case 2: eventSource = (java.lang.CharSequence)value$; break;
    case 3: truckId = (java.lang.Integer)value$; break;
    case 4: driverId = (java.lang.Integer)value$; break;
    case 5: driverName = (java.lang.CharSequence)value$; break;
    case 6: routeId = (java.lang.Integer)value$; break;
    case 7: route = (java.lang.CharSequence)value$; break;
    case 8: speed = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'eventTime' field.
   * @return The value of the 'eventTime' field.
   */
  public java.lang.CharSequence getEventTime() {
    return eventTime;
  }

  /**
   * Sets the value of the 'eventTime' field.
   * @param value the value to set.
   */
  public void setEventTime(java.lang.CharSequence value) {
    this.eventTime = value;
  }

  /**
   * Gets the value of the 'eventTimeLong' field.
   * @return The value of the 'eventTimeLong' field.
   */
  public java.lang.Long getEventTimeLong() {
    return eventTimeLong;
  }

  /**
   * Sets the value of the 'eventTimeLong' field.
   * @param value the value to set.
   */
  public void setEventTimeLong(java.lang.Long value) {
    this.eventTimeLong = value;
  }

  /**
   * Gets the value of the 'eventSource' field.
   * @return The value of the 'eventSource' field.
   */
  public java.lang.CharSequence getEventSource() {
    return eventSource;
  }

  /**
   * Sets the value of the 'eventSource' field.
   * @param value the value to set.
   */
  public void setEventSource(java.lang.CharSequence value) {
    this.eventSource = value;
  }

  /**
   * Gets the value of the 'truckId' field.
   * @return The value of the 'truckId' field.
   */
  public java.lang.Integer getTruckId() {
    return truckId;
  }

  /**
   * Sets the value of the 'truckId' field.
   * @param value the value to set.
   */
  public void setTruckId(java.lang.Integer value) {
    this.truckId = value;
  }

  /**
   * Gets the value of the 'driverId' field.
   * @return The value of the 'driverId' field.
   */
  public java.lang.Integer getDriverId() {
    return driverId;
  }

  /**
   * Sets the value of the 'driverId' field.
   * @param value the value to set.
   */
  public void setDriverId(java.lang.Integer value) {
    this.driverId = value;
  }

  /**
   * Gets the value of the 'driverName' field.
   * @return The value of the 'driverName' field.
   */
  public java.lang.CharSequence getDriverName() {
    return driverName;
  }

  /**
   * Sets the value of the 'driverName' field.
   * @param value the value to set.
   */
  public void setDriverName(java.lang.CharSequence value) {
    this.driverName = value;
  }

  /**
   * Gets the value of the 'routeId' field.
   * @return The value of the 'routeId' field.
   */
  public java.lang.Integer getRouteId() {
    return routeId;
  }

  /**
   * Sets the value of the 'routeId' field.
   * @param value the value to set.
   */
  public void setRouteId(java.lang.Integer value) {
    this.routeId = value;
  }

  /**
   * Gets the value of the 'route' field.
   * @return The value of the 'route' field.
   */
  public java.lang.CharSequence getRoute() {
    return route;
  }

  /**
   * Sets the value of the 'route' field.
   * @param value the value to set.
   */
  public void setRoute(java.lang.CharSequence value) {
    this.route = value;
  }

  /**
   * Gets the value of the 'speed' field.
   * @return The value of the 'speed' field.
   */
  public java.lang.Integer getSpeed() {
    return speed;
  }

  /**
   * Sets the value of the 'speed' field.
   * @param value the value to set.
   */
  public void setSpeed(java.lang.Integer value) {
    this.speed = value;
  }

  /**
   * Creates a new TruckSpeedEvent RecordBuilder.
   * @return A new TruckSpeedEvent RecordBuilder
   */
  public static cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder newBuilder() {
    return new cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder();
  }

  /**
   * Creates a new TruckSpeedEvent RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new TruckSpeedEvent RecordBuilder
   */
  public static cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder newBuilder(cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder other) {
    return new cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder(other);
  }

  /**
   * Creates a new TruckSpeedEvent RecordBuilder by copying an existing TruckSpeedEvent instance.
   * @param other The existing instance to copy.
   * @return A new TruckSpeedEvent RecordBuilder
   */
  public static cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder newBuilder(cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent other) {
    return new cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder(other);
  }

  /**
   * RecordBuilder for TruckSpeedEvent instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<TruckSpeedEvent>
    implements org.apache.avro.data.RecordBuilder<TruckSpeedEvent> {

    private java.lang.CharSequence eventTime;
    private long eventTimeLong;
    private java.lang.CharSequence eventSource;
    private int truckId;
    private int driverId;
    private java.lang.CharSequence driverName;
    private int routeId;
    private java.lang.CharSequence route;
    private int speed;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.eventTime)) {
        this.eventTime = data().deepCopy(fields()[0].schema(), other.eventTime);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.eventTimeLong)) {
        this.eventTimeLong = data().deepCopy(fields()[1].schema(), other.eventTimeLong);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.eventSource)) {
        this.eventSource = data().deepCopy(fields()[2].schema(), other.eventSource);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.truckId)) {
        this.truckId = data().deepCopy(fields()[3].schema(), other.truckId);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.driverId)) {
        this.driverId = data().deepCopy(fields()[4].schema(), other.driverId);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.driverName)) {
        this.driverName = data().deepCopy(fields()[5].schema(), other.driverName);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.routeId)) {
        this.routeId = data().deepCopy(fields()[6].schema(), other.routeId);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.route)) {
        this.route = data().deepCopy(fields()[7].schema(), other.route);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.speed)) {
        this.speed = data().deepCopy(fields()[8].schema(), other.speed);
        fieldSetFlags()[8] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing TruckSpeedEvent instance
     * @param other The existing instance to copy.
     */
    private Builder(cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.eventTime)) {
        this.eventTime = data().deepCopy(fields()[0].schema(), other.eventTime);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.eventTimeLong)) {
        this.eventTimeLong = data().deepCopy(fields()[1].schema(), other.eventTimeLong);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.eventSource)) {
        this.eventSource = data().deepCopy(fields()[2].schema(), other.eventSource);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.truckId)) {
        this.truckId = data().deepCopy(fields()[3].schema(), other.truckId);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.driverId)) {
        this.driverId = data().deepCopy(fields()[4].schema(), other.driverId);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.driverName)) {
        this.driverName = data().deepCopy(fields()[5].schema(), other.driverName);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.routeId)) {
        this.routeId = data().deepCopy(fields()[6].schema(), other.routeId);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.route)) {
        this.route = data().deepCopy(fields()[7].schema(), other.route);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.speed)) {
        this.speed = data().deepCopy(fields()[8].schema(), other.speed);
        fieldSetFlags()[8] = true;
      }
    }

    /**
      * Gets the value of the 'eventTime' field.
      * @return The value.
      */
    public java.lang.CharSequence getEventTime() {
      return eventTime;
    }

    /**
      * Sets the value of the 'eventTime' field.
      * @param value The value of 'eventTime'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setEventTime(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.eventTime = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'eventTime' field has been set.
      * @return True if the 'eventTime' field has been set, false otherwise.
      */
    public boolean hasEventTime() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'eventTime' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearEventTime() {
      eventTime = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'eventTimeLong' field.
      * @return The value.
      */
    public java.lang.Long getEventTimeLong() {
      return eventTimeLong;
    }

    /**
      * Sets the value of the 'eventTimeLong' field.
      * @param value The value of 'eventTimeLong'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setEventTimeLong(long value) {
      validate(fields()[1], value);
      this.eventTimeLong = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'eventTimeLong' field has been set.
      * @return True if the 'eventTimeLong' field has been set, false otherwise.
      */
    public boolean hasEventTimeLong() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'eventTimeLong' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearEventTimeLong() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'eventSource' field.
      * @return The value.
      */
    public java.lang.CharSequence getEventSource() {
      return eventSource;
    }

    /**
      * Sets the value of the 'eventSource' field.
      * @param value The value of 'eventSource'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setEventSource(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.eventSource = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'eventSource' field has been set.
      * @return True if the 'eventSource' field has been set, false otherwise.
      */
    public boolean hasEventSource() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'eventSource' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearEventSource() {
      eventSource = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'truckId' field.
      * @return The value.
      */
    public java.lang.Integer getTruckId() {
      return truckId;
    }

    /**
      * Sets the value of the 'truckId' field.
      * @param value The value of 'truckId'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setTruckId(int value) {
      validate(fields()[3], value);
      this.truckId = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'truckId' field has been set.
      * @return True if the 'truckId' field has been set, false otherwise.
      */
    public boolean hasTruckId() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'truckId' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearTruckId() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'driverId' field.
      * @return The value.
      */
    public java.lang.Integer getDriverId() {
      return driverId;
    }

    /**
      * Sets the value of the 'driverId' field.
      * @param value The value of 'driverId'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setDriverId(int value) {
      validate(fields()[4], value);
      this.driverId = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'driverId' field has been set.
      * @return True if the 'driverId' field has been set, false otherwise.
      */
    public boolean hasDriverId() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'driverId' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearDriverId() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'driverName' field.
      * @return The value.
      */
    public java.lang.CharSequence getDriverName() {
      return driverName;
    }

    /**
      * Sets the value of the 'driverName' field.
      * @param value The value of 'driverName'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setDriverName(java.lang.CharSequence value) {
      validate(fields()[5], value);
      this.driverName = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'driverName' field has been set.
      * @return True if the 'driverName' field has been set, false otherwise.
      */
    public boolean hasDriverName() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'driverName' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearDriverName() {
      driverName = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'routeId' field.
      * @return The value.
      */
    public java.lang.Integer getRouteId() {
      return routeId;
    }

    /**
      * Sets the value of the 'routeId' field.
      * @param value The value of 'routeId'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setRouteId(int value) {
      validate(fields()[6], value);
      this.routeId = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'routeId' field has been set.
      * @return True if the 'routeId' field has been set, false otherwise.
      */
    public boolean hasRouteId() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'routeId' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearRouteId() {
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'route' field.
      * @return The value.
      */
    public java.lang.CharSequence getRoute() {
      return route;
    }

    /**
      * Sets the value of the 'route' field.
      * @param value The value of 'route'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setRoute(java.lang.CharSequence value) {
      validate(fields()[7], value);
      this.route = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'route' field has been set.
      * @return True if the 'route' field has been set, false otherwise.
      */
    public boolean hasRoute() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'route' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearRoute() {
      route = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'speed' field.
      * @return The value.
      */
    public java.lang.Integer getSpeed() {
      return speed;
    }

    /**
      * Sets the value of the 'speed' field.
      * @param value The value of 'speed'.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder setSpeed(int value) {
      validate(fields()[8], value);
      this.speed = value;
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'speed' field has been set.
      * @return True if the 'speed' field has been set, false otherwise.
      */
    public boolean hasSpeed() {
      return fieldSetFlags()[8];
    }


    /**
      * Clears the value of the 'speed' field.
      * @return This builder.
      */
    public cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEvent.Builder clearSpeed() {
      fieldSetFlags()[8] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TruckSpeedEvent build() {
      try {
        TruckSpeedEvent record = new TruckSpeedEvent();
        record.eventTime = fieldSetFlags()[0] ? this.eventTime : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.eventTimeLong = fieldSetFlags()[1] ? this.eventTimeLong : (java.lang.Long) defaultValue(fields()[1]);
        record.eventSource = fieldSetFlags()[2] ? this.eventSource : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.truckId = fieldSetFlags()[3] ? this.truckId : (java.lang.Integer) defaultValue(fields()[3]);
        record.driverId = fieldSetFlags()[4] ? this.driverId : (java.lang.Integer) defaultValue(fields()[4]);
        record.driverName = fieldSetFlags()[5] ? this.driverName : (java.lang.CharSequence) defaultValue(fields()[5]);
        record.routeId = fieldSetFlags()[6] ? this.routeId : (java.lang.Integer) defaultValue(fields()[6]);
        record.route = fieldSetFlags()[7] ? this.route : (java.lang.CharSequence) defaultValue(fields()[7]);
        record.speed = fieldSetFlags()[8] ? this.speed : (java.lang.Integer) defaultValue(fields()[8]);
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<TruckSpeedEvent>
    WRITER$ = (org.apache.avro.io.DatumWriter<TruckSpeedEvent>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<TruckSpeedEvent>
    READER$ = (org.apache.avro.io.DatumReader<TruckSpeedEvent>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
