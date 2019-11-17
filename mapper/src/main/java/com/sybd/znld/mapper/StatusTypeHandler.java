package com.sybd.znld.mapper;

import com.sybd.znld.model.Status;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusTypeHandler extends BaseTypeHandler<Status> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int columnIndex, Status status, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(columnIndex, status.getValue());
    }

    @Override
    public Status getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        var value = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : Status.getStatus(value);
    }

    @Override
    public Status getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        var value = resultSet.getInt(columnIndex);
        return resultSet.wasNull() ? null : Status.getStatus(value);
    }

    @Override
    public Status getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        var value = callableStatement.getInt(columnIndex);
        return callableStatement.wasNull() ? null : Status.getStatus(value);
    }
}
