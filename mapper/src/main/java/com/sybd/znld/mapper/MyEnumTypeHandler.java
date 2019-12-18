package com.sybd.znld.mapper;

import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.Status;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyEnumTypeHandler<E extends Enum<?> & IEnum> extends BaseTypeHandler<E> {
    private Class<E> enumClass;
    public MyEnumTypeHandler(Class<E> enumClass){
        this.enumClass = enumClass;
    }
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int columnIndex, E status, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(columnIndex, status.getValue());
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        var value = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : IEnum.getEnum(enumClass, value);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        var value = resultSet.getInt(columnIndex);
        return resultSet.wasNull() ? null : IEnum.getEnum(enumClass, value);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        var value = callableStatement.getInt(columnIndex);
        return callableStatement.wasNull() ? null : IEnum.getEnum(enumClass, value);
    }
}
