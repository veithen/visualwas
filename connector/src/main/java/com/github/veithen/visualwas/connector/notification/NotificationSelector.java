package com.github.veithen.visualwas.connector.notification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.ObjectName;

import com.github.veithen.visualwas.connector.mapped.MappedClass;

@MappedClass("com.ibm.ws.management.event.FilterElement")
public final class NotificationSelector implements NotificationFilter {
    private static final long serialVersionUID = 5020421872211159772L;
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("listenerId", Integer.TYPE),
        new ObjectStreamField("info", NotificationInfo.class)
    };
    
    private static final AtomicInteger listenerIdSeq = new AtomicInteger();
    
    // In the original class, the listenerId identifies a NotificationListener instance. However,
    // NotificationListener instances are irrelevant for the subscription API. Therefore we simply
    // generate a unique ID.
    private int listenerId = listenerIdSeq.incrementAndGet();
    
    private ObjectName name;
    private NotificationFilter filter;
    
    public NotificationSelector(ObjectName name, NotificationFilter filter) {
        this.name = name;
        this.filter = filter;
    }

    @Override
    public boolean isNotificationEnabled(Notification notification) {
        return name.apply((ObjectName)notification.getSource()) && (filter == null || filter.isNotificationEnabled(notification));
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        listenerId = fields.get("listenerId", 0);
        NotificationInfo notificationInfo = (NotificationInfo)fields.get("info", null);
        name = notificationInfo.getName();
        filter = notificationInfo.getFilter();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        PutField fields = stream.putFields();
        fields.put("listenerId", listenerId);
        fields.put("info", new NotificationInfo(name, filter));
        stream.writeFields();
    }
    
    // Instances of this class are used in HashSets. Therefore we should ensure that the hashCode
    // is computed exactly as by the original class. Note that since we generate a unique listenerId,
    // equality (as defined by the original class) is equivalent to identity and we don't need to
    // override equals.
    public int hashCode() {
        int result = 17;
        result = 37 * result + listenerId;
        result = 37 * result + notificationInfoHashCode();
        return result;
    }

    public int notificationInfoHashCode() {
        int result = 17;
        if (name != null) {
            result = 37 * result + name.hashCode();
        }
        if (filter != null) {
            result = 57 * result + filter.hashCode();
        }
        return result;
    }
}
