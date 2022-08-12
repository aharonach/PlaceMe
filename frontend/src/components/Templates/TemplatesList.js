import React, {useEffect} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert, Button} from "react-bootstrap";
import { extractListFromAPI } from "../../utils";
import {LinkContainer} from "react-router-bootstrap";

function TemplatesList() {
    const [templates, error, loading, axiosFetch] = useAxios();

    const columns = {
        name: "Name",
        description: "Description",
        createdTime: "Created Time",
        numberOfAttributes: "Attribues",
    };

    const getData = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: '/templates',
        });
    };

    useEffect(() => {
        getData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            <LinkContainer to="add"><Button>Add Template</Button></LinkContainer>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && templates && <TableList linkTo={{field: 'name', basePath: '/templates/'}} items={extractListFromAPI(templates, 'templateList')} columns={columns} />}
        </>
    )
}

export default TemplatesList;