import React, {useEffect} from "react";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import HtmlForm from "../Forms/HtmlForm";
import {useForm} from "react-hook-form";
import Loading from "../Loading";
import {Alert} from "react-bootstrap";
import { extractListFromAPI, prepareCheckboxGroup } from "../../utils";

export default function Groups({ pupilGroups, onSubmit, updated }) {
    const [groups, error, loading, axiosFetch] = useAxios();

    let methods = useForm({
        defaultValues: {
            groups: extractListFromAPI(pupilGroups, 'groupList', group => group.id.toString() )
        }
    });

    const getGroups = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: '/groups',
        });
    };

    const fields = [
        {
            id: 'groups',
            type: 'checkbox',
            options: extractListFromAPI(groups, 'groupList', prepareCheckboxGroup('id', 'name')),
        }
    ];

    useEffect(() => {
        getGroups();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert> }
            {!loading && !error && (
                <>
                    <h3>Groups</h3>
                    {updated && <Alert variant="success">Groups updated</Alert>}
                    <HtmlForm formProps={methods} fields={fields} submitCallback={onSubmit} submitLabel={"Update Groups"} />
                </>
            )}
        </>
    );
}