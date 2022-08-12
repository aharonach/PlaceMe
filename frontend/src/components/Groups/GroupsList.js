import React, {useEffect} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert, Button} from "react-bootstrap";
import { extractListFromAPI } from "../../utils";
import DeleteAttribute from "../Attributes/DeleteAttribute";
import {LinkContainer} from "react-router-bootstrap";

function GroupsList() {
    const [groups, error, loading, axiosFetch] = useAxios();

    const columns = {
        name: "Name",
        description: "Description",
        createdTime: "Created Time",
        numberOfPupils: "Pupils",
    };

    const getData = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: '/groups',
        });
    };

    useEffect(() => {
        getData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            <LinkContainer to="add"><Button>Add Group</Button></LinkContainer>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && groups && <TableList linkTo={{field: 'name', basePath: '/groups/'}} items={extractListFromAPI(groups, 'groupList')} columns={columns} />}
        </>
    )
}

export default GroupsList;