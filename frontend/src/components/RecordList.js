import React, {useEffect} from 'react';
import useFetchList from "../hooks/useFetchList";
import {LinkContainer} from "react-router-bootstrap";
import {Alert, Button, ButtonGroup} from "react-bootstrap";
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
       updated = false,
        additionalButtons = null,
        mapCallback = null,
        children
    }) {

    const [list, error, loading, axiosFetch, getList] = useFetchList({
        fetchUrl: fetchUrl,
        propertyName: propertyName,
        mapCallback: mapCallback,
    });

    useEffect(() => {
        getList();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [updated]);

    const addButtonRender = addButton && <LinkContainer to="add"><Button>{addButton}</Button></LinkContainer>;

    return (
        <>
            {title}
            <ButtonGroup>
                {addButtonRender}
                {additionalButtons}
            </ButtonGroup>
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && list && <>
                {children}
                <TableList linkTo={{field: linkField, basePath: basePath ?? fetchUrl}} items={list} columns={columns} />
            </>}
        </>
    )
}