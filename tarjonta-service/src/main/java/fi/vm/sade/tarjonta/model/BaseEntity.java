package fi.vm.sade.tarjonta.model;

import jakarta.persistence.*;

import java.io.Serializable;

@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -1482830143396044915L;

    public static final String ID_COLUMN_NAME = "id";
    public static final String VERSION_COLUMN_NAME = "version";

    @Id
    @Column(name = ID_COLUMN_NAME, unique = true, nullable = false)
    @GeneratedValue
    private Long id;

    @Version
    @Column(name = VERSION_COLUMN_NAME, nullable = false)
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof fi.vm.sade.generic.model.BaseEntity && id != null && id.equals(((fi.vm.sade.generic.model.BaseEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return id == null ? super.hashCode() : id.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[id=");
        sb.append(getId());
        sb.append(", version=");
        sb.append(getVersion());
        sb.append("]");

        return sb.toString();
    }
}
