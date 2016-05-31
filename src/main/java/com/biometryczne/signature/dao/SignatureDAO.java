package com.biometryczne.signature.dao;

import com.biometryczne.signature.beans.SignatureJSONBean;
import com.biometryczne.signature.utils.Signature;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by Almi on 2016-05-31.
 */
public class SignatureDAO implements IDAO<SignatureJSONBean> {
    private SessionFactory sf;

    public SignatureDAO(SessionFactory sessionFactory) {
        sf = sessionFactory;
    }

    @Override
    public void create(SignatureJSONBean entity) {
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        s.save(entity);
        t.commit();
        s.close();
    }

    @Override
    public List<SignatureJSONBean> getAll() {
        Session session = sf.openSession();
        Criteria c = session.createCriteria(SignatureJSONBean.class);
        List<SignatureJSONBean> response = c.list();
        session.close();
        return response;
    }

    @Override
    public void update(SignatureJSONBean entity) {
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        s.update(entity);
        t.commit();
        s.close();
    }

    @Override
    public SignatureJSONBean remove(int id) {
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        Criteria criteria = s.createCriteria(SignatureJSONBean.class);
        criteria.add(Restrictions.eq("id", id));
        SignatureJSONBean bean = (SignatureJSONBean) criteria.uniqueResult();
        if(bean != null) {
            s.delete(bean);
        }
        t.commit();
        s.close();
        return bean;
    }

    @Override
    public SignatureJSONBean getById(int id) {
        Session s = sf.openSession();
        Criteria criteria = s.createCriteria(SignatureJSONBean.class);
        criteria.add(Restrictions.eq("id", id));
        SignatureJSONBean bean = (SignatureJSONBean) criteria.uniqueResult();
        s.close();
        return bean;
    }
}
