package com.smartStudy.repositories;

import com.smartStudy.pojo.Notification;
import  java.util.*;

public interface NotificationRepository {
    Notification addOrUpdate(Notification notification);
    List<Notification> getNotifications (Map<String,String> params);
}