import React, {useEffect} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert} from "react-bootstrap";
import { extractListFromAPI } from "../../utils";

function PupilsList() {
    const [pupils, error, loading, axiosFetch] = useAxios();

    const columns = {
        givenId: "Given ID",
        firstName: "First Name",
        lastName: "Last Name",
        gender: "Gender",
        birthDate: "Birth Date",
        age: "Age",
    };

    const getData = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: '/pupils',
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
            {!loading && !error && pupils && <TableList linkTo={{field: 'givenId', basePath: '/pupils/'}} items={extractListFromAPI(pupils, 'pupilList')} columns={columns} />}
        </>
    )
}

export default PupilsList;