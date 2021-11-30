package cc.ejyf.jfly.batis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@MappedTypes(
        {
                Collection.class,
                List.class,
                ArrayList.class,
                LinkedList.class,
                Set.class,
                HashSet.class,
                LinkedHashSet.class,
                TreeSet.class,
                Map.class,
                HashMap.class,
                LinkedHashMap.class,
                TreeMap.class,
                String[].class,
                double[].class,
                boolean[].class,
                int[].class,
                long[].class
        }
)
@MappedJdbcTypes(value = {JdbcType.LONGVARCHAR, JdbcType.LONGNVARCHAR}, includeNullJdbcType = true)
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonTypeHandler.class);
    private Class<T> clazz;
    private ObjectMapper mapper = new ObjectMapper();

    public JsonTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Type argument cannot be null");
        }
        this.clazz = clazz;

    }

    /**
     * object转json string
     *
     * @param object
     * @return
     */
    private String toJSON(T object) {
        try {
            String string = mapper.writeValueAsString(object);
            LOGGER.debug(">>>> json handler string:{} <<<<", string);
            return string;
        } catch (Exception e) {
            LOGGER.error(">>>> covert object to json string failed, error message:{} <<<<", e.getMessage());
        }
        return null;
    }

    /**
     * json转object
     *
     * @param json
     * @param clazz
     * @return
     * @throws JsonProcessingException
     */
    private T toObject(String json, Class<T> clazz) throws JsonProcessingException {
        if (json != null && !"".equals(json)) {
            return mapper.readValue(json, clazz);
        }
        return null;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T t, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJSON(t));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            return toObject(rs.getString(columnName), clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return toObject(rs.getString(columnIndex), clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return toObject(cs.getString(columnIndex), clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

