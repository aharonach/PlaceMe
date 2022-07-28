import React, {useEffect} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert} from "react-bootstrap";

function PupilsList() {
    const [pupils, error, loading, axiosFetch] = useAxios();

    const columns = {
        id: "ID",
        givenId: "Given ID",
        firstName: "First Name",
        lastName: "Last Name",
        gender: "Gender",
        birthDate: "Birth Date",
        createdTime: "Created Time"
    };

    const getData = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: '/pupils',
        });
    };

    useEffect(() => getData(), []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && pupils?._embedded?.pupilList && <TableList basePath="/pupils/" items={pupils._embedded.pupilList} columns={columns} />}
        </>
    )
}

export default PupilsList;