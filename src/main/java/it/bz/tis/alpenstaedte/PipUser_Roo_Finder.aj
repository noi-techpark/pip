// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package it.bz.tis.alpenstaedte;

import it.bz.tis.alpenstaedte.PipUser;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect PipUser_Roo_Finder {
    
    public static Long PipUser.countFindPipUsersByEmailEquals(String email) {
        if (email == null || email.length() == 0) throw new IllegalArgumentException("The email argument is required");
        EntityManager em = PipUser.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM PipUser AS o WHERE o.email = :email", Long.class);
        q.setParameter("email", email);
        return ((Long) q.getSingleResult());
    }
    
    public static Long PipUser.countFindPipUsersByUuidEquals(String uuid) {
        if (uuid == null || uuid.length() == 0) throw new IllegalArgumentException("The uuid argument is required");
        EntityManager em = PipUser.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM PipUser AS o WHERE o.uuid = :uuid", Long.class);
        q.setParameter("uuid", uuid);
        return ((Long) q.getSingleResult());
    }
    
    public static TypedQuery<PipUser> PipUser.findPipUsersByEmailEquals(String email) {
        if (email == null || email.length() == 0) throw new IllegalArgumentException("The email argument is required");
        EntityManager em = PipUser.entityManager();
        TypedQuery<PipUser> q = em.createQuery("SELECT o FROM PipUser AS o WHERE o.email = :email", PipUser.class);
        q.setParameter("email", email);
        return q;
    }
    
    public static TypedQuery<PipUser> PipUser.findPipUsersByEmailEquals(String email, String sortFieldName, String sortOrder) {
        if (email == null || email.length() == 0) throw new IllegalArgumentException("The email argument is required");
        EntityManager em = PipUser.entityManager();
        StringBuilder queryBuilder = new StringBuilder("SELECT o FROM PipUser AS o WHERE o.email = :email");
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            queryBuilder.append(" ORDER BY ").append(sortFieldName);
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                queryBuilder.append(" ").append(sortOrder);
            }
        }
        TypedQuery<PipUser> q = em.createQuery(queryBuilder.toString(), PipUser.class);
        q.setParameter("email", email);
        return q;
    }
    
    public static TypedQuery<PipUser> PipUser.findPipUsersByUuidEquals(String uuid) {
        if (uuid == null || uuid.length() == 0) throw new IllegalArgumentException("The uuid argument is required");
        EntityManager em = PipUser.entityManager();
        TypedQuery<PipUser> q = em.createQuery("SELECT o FROM PipUser AS o WHERE o.uuid = :uuid", PipUser.class);
        q.setParameter("uuid", uuid);
        return q;
    }
    
    public static TypedQuery<PipUser> PipUser.findPipUsersByUuidEquals(String uuid, String sortFieldName, String sortOrder) {
        if (uuid == null || uuid.length() == 0) throw new IllegalArgumentException("The uuid argument is required");
        EntityManager em = PipUser.entityManager();
        StringBuilder queryBuilder = new StringBuilder("SELECT o FROM PipUser AS o WHERE o.uuid = :uuid");
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            queryBuilder.append(" ORDER BY ").append(sortFieldName);
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                queryBuilder.append(" ").append(sortOrder);
            }
        }
        TypedQuery<PipUser> q = em.createQuery(queryBuilder.toString(), PipUser.class);
        q.setParameter("uuid", uuid);
        return q;
    }
    
}