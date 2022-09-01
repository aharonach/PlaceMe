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
        children
    }) {

    const [list, error, loading, axiosFetch, getList] = useFetchList({
        fetchUrl: fetchUrl,
        propertyName: propertyName,
    });

    useEffect(() => {
        getList();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [updated]);

    const addButtonRender = addButton && <LinkContainer to="add"><Button>{addButton}</Button></LinkContainer>;

    return (
        <>
            {title}
            {additionalButtons ? <ButtonGroup>
                {additionalButtons}
                {addButtonRender}
            </ButtonGroup> : addButtonRender}
            <Loading show={loading} />
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && list && <>
                {children}
                <TableList linkTo={{field: linkField, basePath: basePath ?? fetchUrl}} items={list} columns={columns} />
            </>}
        </>
    )
}