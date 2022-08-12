import React, {useEffect} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert, Button} from "react-bootstrap";
import { extractListFromAPI } from "../../utils";
import DeleteAttribute from "../Attributes/DeleteAttribute";

function GroupsList() {
    const [groups, error, loading, axiosFetch] = useAxios();

    const columns = {
        id: "ID",
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
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && groups && <TableList basePath="/groups/" items={extractListFromAPI(groups, 'groupList')} columns={columns} />}
        </>
    )
}

export default GroupsList;