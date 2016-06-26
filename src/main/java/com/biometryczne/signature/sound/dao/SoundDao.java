package com.biometryczne.signature.sound.dao;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.biometryczne.signature.dao.IDAO;
import com.biometryczne.signature.sound.VoiceEntry;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by Almi on 2016-06-26.
 */
public class SoundDao implements IDAO<VoiceEntry> {

    private SessionFactory sf;

    public SoundDao(SessionFactory sessionFactory) {
        this.sf = sessionFactory;
    }

    @Override
    public void create(VoiceEntry entity) {
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        s.save(entity);
        t.commit();
        s.close();
    }

    @Override
    public List<VoiceEntry> getAll() {
        Session session = sf.openSession();
        Criteria c = session.createCriteria(VoiceEntry.class);
        List<VoiceEntry> response = c.list();
        session.close();
        return response;
    }

    @Override
    public void update(VoiceEntry entity) {
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        s.update(entity);
        t.commit();
        s.close();
    }

    @Override
    public VoiceEntry remove(int id) {
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        Criteria criteria = s.createCriteria(VoiceEntry.class);
        criteria.add(Restrictions.eq("id", id));
        VoiceEntry bean = (VoiceEntry) criteria.uniqueResult();
        if(bean != null) {
            s.delete(bean);
        }
        t.commit();
        s.close();
        return bean;
    }

    @Override
    public VoiceEntry getById(int id) {
        return null;
    }

    public VoiceEntry getByName(String userKey) {
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        Criteria criteria = s.createCriteria(VoiceEntry.class);
        criteria.add(Restrictions.eq("name", userKey));
        VoiceEntry bean = (VoiceEntry) criteria.uniqueResult();
        t.commit();
        s.close();
        return bean;
    }
}
