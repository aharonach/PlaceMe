import React, {useEffect} from 'react';
import useFetchList from "../hooks/useFetchList";
import {LinkContainer} from "react-router-bootstrap";
import {Alert, Button} from "react-bootstrap";
import Loading from "./Loading";
import TableList from "./TableList";

export default function RecordList(
    {
       fetchUrl = '',
        propertyName = '',
       addButton = '',
       title = '',
       columns = {},
        basePath = '',
        linkField = '',
       updated = false
    }) {

    const [list, error, loading, axiosFetch, getList] = useFetchList({
        fetchUrl: fetchUrl,
        propertyName: propertyName,
    });

    useEffect(() => {
        getList();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [updated]);

    return (
        <>
            {title}
            {addButton && <LinkContainer to="add"><Button>{addButton}</Button></LinkContainer>}
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && list && <TableList linkTo={{field: linkField, basePath: basePath ?? fetchUrl}} items={list} columns={columns} />}
        </>
    )
}